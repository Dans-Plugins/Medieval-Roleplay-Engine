package plugin;

public class CharacterCard {
    private String name = "";
    private String race = "";
    private String subculture = "";
    private int age = 0;

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

    void setAge(int newAge) {
        age = newAge;
    }

    int getAge() {
        return age;
    }
}
