package pk_tnuv_mis.zaiba;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    static class Location {
        String city;
        String location;
        int backgroundColor;

        Location(String city, String location, int backgroundColor) {
            this.city = city;
            this.location = location;
            this.backgroundColor = backgroundColor;
        }
    }

    static class FrogTraffic {
        String title;
        String stat;

        FrogTraffic(String title, String stat) {
            this.title = title;
            this.stat = stat;
        }
    }

    static class Article {
        private final String title;
        private final String text;
        private final String boldProlog;
        private final String article;
        private final List<Integer> imageResources;
        private final String link;

        Article(String title, String text, String boldProlog, String article, List<Integer> imageResources, String link) {
            this.title = title;
            this.text = text;
            this.boldProlog = boldProlog;
            this.article = article;
            this.imageResources = imageResources;
            this.link = link;
        }

        public String getTitle() { return title; }
        public String getText() { return text; }
        public String getBoldProlog() { return boldProlog; }
        public String getArticle() { return article; }
        public List<Integer> getImageResources() { return imageResources; }
        public String getLink() { return link; }
    }

    ConstraintLayout frogOverlay;
    TextView factText;
    Button shareButton, cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataManager.initialize(this); // Initialize data

        setupBottomMenu();

        List<Location> locations = Arrays.asList(
                new Location("Ljubljana", "Večna pot", ContextCompat.getColor(this, R.color.card_1)),
                new Location("Novo mesto", "Straža", ContextCompat.getColor(this, R.color.card_2)),
                new Location("Vrhnika", "Lesno Brdo", ContextCompat.getColor(this, R.color.card_3)),
                new Location("Trebnje", "Zabrdje", ContextCompat.getColor(this, R.color.card_4))
        );

        List<FrogTraffic> frogTrafficStats = Arrays.asList(
                new FrogTraffic("🐾 Navadna krastača", rndNumber()),
                new FrogTraffic("🐾 Zelena krastača", rndNumber()),
                new FrogTraffic("🐾 Zelena rega", rndNumber()),
                new FrogTraffic("🐾 Sekulja", rndNumber())
        );

        List<Article> articles = loadArticlesFromJson();

        CardView card1, card2, card3, card4;
        TextView city1, location1, city2, location2, city3, location3, city4, location4;

        int[] locationCardIds = {R.id.card1, R.id.card2, R.id.card3, R.id.card4};
        for (int i = 0; i < locationCardIds.length; i++) {
            CardView card = findViewById(locationCardIds[i]);
            if (card != null) {
                Location location = locations.get(i);
                TextView cityName = card.findViewById(R.id.city);
                TextView locationName = card.findViewById(R.id.location);
                if (cityName != null) cityName.setText(location.city);
                if (locationName != null) locationName.setText(location.location);
                card.setCardBackgroundColor(location.backgroundColor);
                final String locationText = location.location;
                card.setOnClickListener(v -> Toast.makeText(this, locationText + " clicked", Toast.LENGTH_SHORT).show());
            }
        }

        int[] frogTrafficCardIds = {R.id.card5, R.id.card6, R.id.card7, R.id.card8};
        for (int i = 0; i < frogTrafficCardIds.length; i++) {
            CardView card = findViewById(frogTrafficCardIds[i]);
            if (card != null) {
                FrogTraffic stat = frogTrafficStats.get(i);
                TextView title = card.findViewById(R.id.traffic_title);
                TextView statText = card.findViewById(R.id.traffic_stat);
                if (title != null) title.setText(stat.title);
                if (statText != null) statText.setText(stat.stat);
                final String statTitle = stat.title;
                final String statValue = stat.stat;
                card.setOnClickListener(v -> Toast.makeText(this, statTitle + ": " + statValue, Toast.LENGTH_SHORT).show());
            }
        }

        int[] articleCardIds = {R.id.article_card_1, R.id.article_card_2};
        for (int i = 0; i < Math.min(articleCardIds.length, articles.size()); i++) {
            CardView card = findViewById(articleCardIds[i]);
            if (card != null) {
                Article article = articles.get(i);
                ImageView imageView = card.findViewById(R.id.article_image);
                TextView title = card.findViewById(R.id.article_title);
                TextView source = card.findViewById(R.id.article_source);
                if (imageView != null && article.getImageResources() != null && !article.getImageResources().isEmpty()) {
                    imageView.setImageResource(article.getImageResources().get(0));
                } else if (imageView != null) {
                    imageView.setImageResource(R.drawable.info);
                }
                if (title != null) title.setText(article.getTitle());
                if (source != null) source.setText(article.getLink());
                final int articleIndex = i;
                card.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, FifthActivity.class);
                    intent.putExtra("article_index", articleIndex);
                    startActivity(intent);
                });
            }
        }

        TextView frogFactText = findViewById(R.id.frogFactText);
        if (frogFactText != null) {
            frogFactText.setText(getString(R.string.lorem_ipsum));
        }

        frogOverlay = findViewById(R.id.frog_fact_overlay);
        factText = findViewById(R.id.factText);
        shareButton = findViewById(R.id.shareButton);
        cancelButton = findViewById(R.id.cancelButton);

        CardView frogFactCard = findViewById(R.id.frogFactCard);
        frogFactCard.setOnClickListener(v -> {
            factText.setText(R.string.fact_1);
            frogOverlay.setVisibility(View.VISIBLE);
        });

        cancelButton.setOnClickListener(v -> frogOverlay.setVisibility(View.GONE));

        shareButton.setOnClickListener(v -> {
            String textToShare = factText.getText().toString();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
            startActivity(Intent.createChooser(shareIntent, "Share"));
        });

        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);

        city1 = card1.findViewById(R.id.city);
        location1 = card1.findViewById(R.id.location);

        city2 = card2.findViewById(R.id.city);
        location2 = card2.findViewById(R.id.location);

        city3 = card3.findViewById(R.id.city);
        location3 = card3.findViewById(R.id.location);

        city4 = card4.findViewById(R.id.city);
        location4 = card4.findViewById(R.id.location);

        setCardClickListener(card1, city1, location1);
        setCardClickListener(card2, city2, location2);
        setCardClickListener(card3, city3, location3);
        setCardClickListener(card4, city4, location4);
    }

    private List<Article> loadArticlesFromJson() {
        List<Article> articleList = new ArrayList<>();
        try {
            InputStream is = getAssets().open("articles.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String title = jsonObject.getString("title");
                String text = jsonObject.getString("text");
                JSONObject actualText = jsonObject.getJSONObject("actualText");
                String boldProlog = actualText.getString("boldProlog");
                String article = actualText.getString("article");
                JSONArray images = jsonObject.getJSONArray("images");
                List<Integer> imageResources = new ArrayList<>();
                for (int j = 0; j < images.length(); j++) {
                    String imageName = images.getString(j);
                    int resId = getResources().getIdentifier(imageName, "drawable", getPackageName());
                    if (resId != 0) {
                        imageResources.add(resId);
                    } else {
                        imageResources.add(R.drawable.info); // Fallback if image not found
                    }
                }
                String link = jsonObject.getString("link");

                articleList.add(new Article(title, text, boldProlog, article, imageResources, link));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articleList;
    }

    private String getDate() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String dayOfWeekString = "";
        switch (dayOfWeek) {
            case Calendar.SUNDAY: dayOfWeekString = "Sunday"; break;
            case Calendar.MONDAY: dayOfWeekString = "Monday"; break;
            case Calendar.TUESDAY: dayOfWeekString = "Tuesday"; break;
            case Calendar.WEDNESDAY: dayOfWeekString = "Wednesday"; break;
            case Calendar.THURSDAY: dayOfWeekString = "Thursday"; break;
            case Calendar.FRIDAY: dayOfWeekString = "Friday"; break;
            case Calendar.SATURDAY: dayOfWeekString = "Saturday"; break;
        }
        return "bar_" + dayOfWeekString.toLowerCase();
    }

    private void setupBottomMenu() {
        ImageButton homeButton = findViewById(R.id.menu_bar_button_home);
        ImageButton infoButton = findViewById(R.id.menu_bar_button_info);
        ImageButton settingsButton = findViewById(R.id.menu_bar_button_settings);

        homeButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(homeIntent);
        });

        infoButton.setOnClickListener(v -> {
            Intent infoIntent = new Intent(MainActivity.this, ThirdActivity.class);
            startActivity(infoIntent);
        });

        settingsButton.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(MainActivity.this, FourthActivity.class);
            startActivity(settingsIntent);
        });
    }

    private String rndNumber() {
        return String.valueOf(new Random().nextInt(100));
    }

    private void setCardClickListener(CardView card, TextView city, TextView location) { // Updated to CardView
        card.setOnClickListener(v -> {
            String cityName = city.getText().toString();
            String locationName = location.getText().toString();
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            intent.putExtra("CITY_NAME", cityName);
            intent.putExtra("LOCATION_NAME", locationName);
            startActivity(intent);
        });
    }
}
    /*
    private void setupMQTT() throws MqttException {
        String serverUri = "ssl://eu1.cloud.thethings.network:8883";
        String clientId = MqttClient.generateClientId();
        String username = "zabe-mis";
        String password = "NNSXS.AHM2NMEL7QRNDLC7MIO3E73T4T7BJMLLMBAWXKY.UGEVFE2IY36FBFX5I3OP7WH75LK6RN4QVVZR4JSUEDQ3HNOJECNA";

        mqttClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());

        mqttClient.connect(options, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d(TAG, "Connected to TTN MQTT!");
                subscribeToTopic();
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.e(TAG, "MQTT connection failed", exception);
            }
        });
    }

    private void subscribeToTopic() {
        String topic = "v3/zabe-mis/devices/zabe-senzor-1/up";
        try {
            mqttClient.subscribe(topic, 1, (topic1, message) -> {
                String payload = new String(message.getPayload());
                Log.d(TAG, "Received message: " + payload);
                // Parse JSON or update UI here
            });
        } catch (MqttException e) {
            Log.e(TAG, "Subscription error", e);
        }
    }
    */


