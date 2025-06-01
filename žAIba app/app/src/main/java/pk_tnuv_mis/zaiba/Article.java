package pk_tnuv_mis.zaiba;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Article {
    private String title;
    private String text;
    @SerializedName("images")
    private List<String> imageNames; // Store image names from JSON
    private String link;

    // Getters
    public String getTitle() { return title; }
    public String getText() { return text; }
    public List<String> getImageNames() { return imageNames; }
    public String getLink() { return link; }

    // Method to get drawable resource IDs (to be implemented in DataManager)
    public List<Integer> getImageResources() {
        return DataManager.getImageResources(imageNames);
    }
}
