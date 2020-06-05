package plugin;

public class CharacterCard {
    private String name = "defaultName";
    private String race = "defaultRace";
    private String subculture = "defaultSubculture";
    private int age = 0;
    private String gender = "defaultGender";
    private String religion = "defaultReligion";

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

    void setGender(String newGender) {
        gender = newGender;
    }

    String getGender() {
        return gender;
    }

    void setReligion(String newReligion) {
        religion = newReligion;
    }

    String getReligion() {
        return religion;
    }

}
