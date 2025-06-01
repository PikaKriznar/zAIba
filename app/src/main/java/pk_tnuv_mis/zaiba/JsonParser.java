package pk_tnuv_mis.zaiba;

import org.json.JSONObject;

public class JsonParser {
    public static String parseUplink(String jsonPayload) {
        try {
            JSONObject root = new JSONObject(jsonPayload);

            // TODO: Uredi JSON branje glede na format
            if (root.has("uplink_message")) {
                root = root.getJSONObject("uplink_message").getJSONObject("decoded_payload");
            }
            int prediction = root.getInt("prediction");
            int confidence = root.getInt("confidence");

            return "Prediction: " + prediction + "\nConfidence: " + confidence + "%";
        } catch (Exception e) {
            return "Error parsing JSON: " + e.getMessage();
        }
    }
}
