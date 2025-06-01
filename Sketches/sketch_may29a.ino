#include <Arduino.h>
#include <Adafruit_TinyUSB.h>
#include <PDM.h> // For PDM microphone
#include <zAIba_final_inferencing.h> // First Edge Impulse model

// Audio settings
#define SAMPLE_RATE 16000 // 16 kHz
#define CHUNK_DURATION 1 // 1 second per chunk
#define CHUNK_SIZE (SAMPLE_RATE * CHUNK_DURATION) // 16,000 samples = 32 KB
#define TOTAL_DURATION 10 // Total 10 seconds
#define NUM_CHUNKS (TOTAL_DURATION / CHUNK_DURATION) // 10 chunks
int16_t audio_chunk[CHUNK_SIZE]; // Buffer for 1-second audio
volatile int samples_collected = 0;

// PDM settings
#define PDM_BUFFER_SIZE 256
int16_t pdm_buffer[PDM_BUFFER_SIZE];

void onPDMdata() {
  int bytesAvailable = PDM.available();
  PDM.read(pdm_buffer, bytesAvailable);
  for (int i = 0; i < bytesAvailable / 2 && samples_collected < CHUNK_SIZE; i++) {
    audio_chunk[samples_collected++] = pdm_buffer[i];
  }
}

void setup() {
  // Turn ON LoRaWAN Modem
  pinMode(5, OUTPUT);
  digitalWrite(5, HIGH);

  // Initialize Serial for debugging
  Serial.begin(9600);
  while (!Serial) {}

  // Initialize Serial1 for LoRaWAN modem
  Serial1.begin(9600);
  while (!Serial1) {}

  // Configure LoRaWAN
  Serial1.println("AT+ID=AppEui,\"2444666668888888\""); delay(1000);
  Serial1.println("AT+ID=DevEui,\"70B3D57ED0070833\""); delay(1000);
  Serial1.println("AT+KEY=AppKey,\"2C6914E971CAF495565BC596684F81FC\""); delay(1000);
  Serial1.println("AT+MODE=LWOTAA"); delay(1000);
  Serial1.println("AT+ADR=OFF"); delay(1000);
  Serial1.println("AT+DR=DR0"); delay(1000);
  Serial1.println("AT+JOIN"); delay(5000);

  // Initialize PDM microphone
  PDM.onReceive(onPDMdata);
  PDM.begin(1, SAMPLE_RATE); // Mono, 16 kHz
}

void loop() {
  bool frogs_detected = false;
  String final_label = "no";

  // Process 10 chunks of 1 second each
  for (int chunk = 0; chunk < NUM_CHUNKS; chunk++) {
    // Reset chunk buffer
    samples_collected = 0;
    memset(audio_chunk, 0, sizeof(audio_chunk));

    // Collect 1 second of audio
    Serial.println("Recording chunk " + String(chunk + 1));
    unsigned long start_time = millis();
    while (millis() - start_time < CHUNK_DURATION * 1000 && samples_collected < CHUNK_SIZE) {
      // Audio collected in onPDMdata callback
    }

    // Run yes_no model
    ei_impulse_result_t result = {0};
    signal_t signal;
    signal.total_length = CHUNK_SIZE;
    signal.get_data = [](size_t offset, size_t length, float *out_ptr) -> int {
      for (size_t i = 0; i < length; i++) {
        out_ptr[i] = (float)audio_chunk[offset + i] / 32768.0f; // Convert int16 to float
      }
      return 0;
    };

    EI_IMPULSE_ERROR res = run_classifier(&signal, &result, false);
    if (res != EI_IMPULSE_OK) {
      Serial.println("Error running model on chunk " + String(chunk + 1));
      continue;
    }

    // Get highest confidence class
    String class_label = "";
    float max_confidence = 0.0;
    for (size_t i = 0; i < EI_CLASSIFIER_LABEL_COUNT; i++) {
      if (result.classification[i].value > max_confidence) {
        max_confidence = result.classification[i].value;
        class_label = result.classification[i].label;
      }
    }
    Serial.println("Chunk " + String(chunk + 1) + " result: " + class_label + " (" + String(max_confidence) + ")");

    // If "yes" (frogs present) is detected, update final result
    if (class_label == "yes") {
      frogs_detected = true;
      final_label = "yes";
    }
  }

  PDM.end(); // Stop PDM after recording

  // Send final classification result to TTN
  String payload = "AT+MSG=\"" + final_label + "\"";
  Serial1.println(payload);
  Serial.println("Sent to TTN: " + final_label);

  // Respect LoRaWAN duty cycle (36 seconds for DR0 in EU868)
  delay(36000);
}