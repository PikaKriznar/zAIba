package pk_tnuv_mis.zaiba;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {
    private static List<Article> articles = new ArrayList<>();
    private static List<Frog> frogs = new ArrayList<>();
    private static Map<String, Integer> imageResourceMap = new HashMap<>();

    static {
        imageResourceMap.put("zaba1", R.drawable.zaba1);
        imageResourceMap.put("zaba2", R.drawable.zaba2);
        imageResourceMap.put("zaba22", R.drawable.zaba22);
        imageResourceMap.put("zaba3", R.drawable.zaba3);
        imageResourceMap.put("zaba4", R.drawable.zaba4);
        imageResourceMap.put("akcija_img", R.drawable.akcija_img);
        imageResourceMap.put("akcija_article", R.drawable.akcija_article);
    }

    public static void initialize(Context context) {
        loadArticles(context);
        loadFrogs(context);
    }

    private static void loadArticles(Context context) {
        try {
            InputStream is = context.getAssets().open("articles.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            articles = new Gson().fromJson(json, new TypeToken<List<Article>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadFrogs(Context context) {
        try {
            InputStream is = context.getAssets().open("frogs.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            frogs = new Gson().fromJson(json, new TypeToken<List<Frog>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Article> getArticles() {
        return new ArrayList<>(articles);
    }

    public static List<Frog> getFrogs() {
        return new ArrayList<>(frogs);
    }

    public static List<Integer> getImageResources(List<String> imageNames) {
        List<Integer> resources = new ArrayList<>();
        for (String name : imageNames) {
            Integer resId = imageResourceMap.get(name);
            if (resId != null) {
                resources.add(resId);
            } else {
                System.out.println("Image not found for name: " + name); // Debug log
            }
        }
        return resources;
    }

    // Add method to get single image resource for Frog
    public static Integer getImageResource(String imageName) {
        Integer resId = imageResourceMap.get(imageName);
        if (resId == null) {
            System.out.println("Image not found for name: " + imageName);
        }
        return resId;
    }
}