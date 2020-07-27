package rpsystem.Objects;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CharacterCard {
    private String playerName = "defaultPlayerName";
    private String name = "defaultName";
    private String race = "defaultRace";
    private String subculture = "defaultSubculture";
    private int age = 0;
    private String gender = "defaultGender";
    private String religion = "defaultReligion";

    public CharacterCard(String nameOfPlayer) {
        playerName = nameOfPlayer;
    }

    void setPlayerName(String newName) {
        playerName = newName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setName(String newName) {
        name = newName;
    }

    public String getName() {
        return name;
    }

    public void setRace(String newRace) {
        race = newRace;
    }

    public String getRace() {
        return race;
    }

    public void setSubculture(String newSubculture) {
        subculture = newSubculture;
    }

    public String getSubculture() {
        return subculture;
    }

    public void setAge(int newAge) {
        age = newAge;
    }

    public int getAge() {
        return age;
    }

    public void setGender(String newGender) {
        gender = newGender;
    }

    public String getGender() {
        return gender;
    }

    public void setReligion(String newReligion) {
        religion = newReligion;
    }

    public String getReligion() {
        return religion;
    }

    public boolean save() {
        try {
            File saveFolder = new File("./plugins/medieval-roleplay-engine/");
            if (!saveFolder.exists()) {
                saveFolder.mkdir();
            }
            File saveFile = new File("./plugins/medieval-roleplay-engine/" + playerName + ".txt");
            if (saveFile.createNewFile()) {
                System.out.println("Save file for character card belonging to " + playerName + " created.");
            } else {
                System.out.println("Save file for character card belonging to " + playerName + " already exists. Altering.");
            }

            FileWriter saveWriter = new FileWriter("./plugins/medieval-roleplay-engine/" + playerName + ".txt");

            // actual saving takes place here
            saveWriter.write(playerName + "\n");
            saveWriter.write(name + "\n");
            saveWriter.write(race + "\n");
            saveWriter.write(subculture + "\n");
            saveWriter.write(age + "\n");
            saveWriter.write(gender + "\n");
            saveWriter.write(religion + "\n");

            saveWriter.close();

            System.out.println("Successfully saved character card belonging to " + playerName + ".");
            return true;

        } catch (IOException e) {
            System.out.println("An error occurred saving character card belonging to " + playerName);
            e.printStackTrace();
            return false;
        }
    }

    public boolean load(String filename) {
        try {
            File loadFile = new File("./plugins/medieval-roleplay-engine/" + filename);
            Scanner loadReader = new Scanner(loadFile);

            // actual loading
            if (loadReader.hasNextLine()) {
                setPlayerName(loadReader.nextLine());
            }
            if (loadReader.hasNextLine()) {
                setName(loadReader.nextLine());
            }
            if (loadReader.hasNextLine()) {
                setRace(loadReader.nextLine());
            }
            if (loadReader.hasNextLine()) {
                setSubculture(loadReader.nextLine());
            }
            if (loadReader.hasNextLine()) {
                setAge(Integer.parseInt(loadReader.nextLine()));
            }
            if (loadReader.hasNextLine()) {
                setGender(loadReader.nextLine());
            }
            if (loadReader.hasNextLine()) {
                setReligion(loadReader.nextLine());
            }

            loadReader.close();
            System.out.println("Character card belonging to" + playerName + " successfully loaded.");
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred loading the file " + filename + ".");
            e.printStackTrace();
            return false;
        }
    }

}
