package plugin;

public class CharacterCard {
    private String name;
    private String race;
    private String subculture;
    private String regionOfOrigin;

    void setName(String newName) {
        name = newName;
    }

    String getName() {
        return name;
    }

    void setRace(String newRace) {
        race = newRace;
    }

    String getRace() {
        return race;
    }

    void setSubculture(String newSubculture) {
        subculture = newSubculture;
    }

    String getSubculture() {
        return subculture;
    }

    void setRegionOfOrigin(String newRegion) {
        regionOfOrigin = newRegion;
    }

    String getRegionOfOrigin() {
        return regionOfOrigin;
    }
}
