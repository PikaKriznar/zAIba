package pk_tnuv_mis.zaiba;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SixthActivity extends AppCompatActivity {

    private TextView frogTitle, scientificName, frogDescription, frogHabitat, topTitle;
    private ImageView frogImage;
    private Button playButton;

    public static final String EXTRA_FROG_NAME = "frog_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sixth);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        topTitle =findViewById(R.id.location_smallTitle);
        frogTitle = findViewById(R.id.frogTitle);
        scientificName = findViewById(R.id.scientificName);
        frogImage = findViewById(R.id.frogImage);
        frogDescription = findViewById(R.id.frogDescription);
        frogHabitat = findViewById(R.id.frogHabitat);
        playButton = findViewById(R.id.playButton);

        // Get frog name from Intent
        String frogName = getIntent().getStringExtra(EXTRA_FROG_NAME);
        if (frogName == null) {
            Toast.makeText(this, "Error: Frog name not provided", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Load and parse JSON
        try {
            String jsonString = loadJSONFromAsset("frogs.json");
            JSONArray frogsArray = new JSONArray(jsonString);

            // Find the frog by name
            JSONObject selectedFrog = null;
            for (int i = 0; i < frogsArray.length(); i++) {
                JSONObject frog = frogsArray.getJSONObject(i);
                if (frog.getString("name").equals(frogName)) {
                    selectedFrog = frog;
                    break;
                }
            }

            if (selectedFrog != null) {
                topTitle.setText(selectedFrog.getString("name"));
                frogTitle.setText(selectedFrog.getString("name"));
                scientificName.setText(selectedFrog.getString("latinName"));
                frogDescription.setText(selectedFrog.getString("basicDescription"));
                frogHabitat.setText(selectedFrog.getString("habitat"));

                // Set image
                String imageName = selectedFrog.getString("image");
                int imageResId = getResources().getIdentifier(imageName, "drawable", getPackageName());
                if (imageResId != 0) {
                    frogImage.setImageResource(imageResId);
                } else {
                    Log.w("SixthActivity", "Image resource not found: " + imageName);
                }

                // Set play button listener (placeholder)
                String soundFile = selectedFrog.getString("soundFile");
                playButton.setOnClickListener(v -> {
                    Toast.makeText(this, "Play sound: " + soundFile, Toast.LENGTH_SHORT).show();
                    // Add audio playback logic here
                });
            } else {
                Log.e("SixthActivity", frogName + " not found in JSON");
                Toast.makeText(this, "Error: Frog data not found", Toast.LENGTH_LONG).show();
                finish();
            }

        } catch (Exception e) {
            Log.e("SixthActivity", "Error parsing JSON", e);
            Toast.makeText(this, "Error loading frog data", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private String loadJSONFromAsset(String fileName) {
        try {
            InputStream is = getAssets().open(fileName);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e("SixthActivity", "Error reading JSON file", e);
            return null;
        }
    }
}