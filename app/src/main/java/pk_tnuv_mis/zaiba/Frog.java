package pk_tnuv_mis.zaiba;

import java.util.List;

public class Frog {
    private String name;
    private String latinName;
    private String image;
    private String description;
    private String basicDescription;
    private MapData mapCoordinates;
    private String habitat;
    private List<String> biologyStages;
    private String soundFile;
    private String estReadingTime;

    // Getters
    public String getName() { return name; }
    public String getLatinName() { return latinName; }
    public String getImage() { return image; }
    public String getDescription() { return description; }
    public String getBasicDescription() { return basicDescription; }
    public MapData getMapCoordinates() { return mapCoordinates; }
    public String getHabitat() { return habitat; }
    public List<String> getBiologyStages() { return biologyStages; }
    public String getSoundFile() { return soundFile; }
    public String getEstReadingTime() { return estReadingTime; }

    // Method to get image resource ID
    public Integer getImageResource() {
        return DataManager.getImageResource(image);
    }
}

// frogs.json viri:
// Zelena rega: https://www.ckff.si/icvds/vrste-dvozivk/hyla-arborea
// Navadna krastača: https://www.ckff.si/icvds/vrste-dvozivk/bufo-bufo
// Zelena krastača: https://www.ckff.si/icvds/vrste-dvozivk/bufotes-viridis
// Sekulja: https://www.ckff.si/icvds/vrste-dvozivk/rana-temporaria