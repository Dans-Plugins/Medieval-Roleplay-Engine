package dansplugins.rpsystem.cards;

import dansplugins.rpsystem.MedievalRoleplayEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;

public class CharacterCard {
    private final MedievalRoleplayEngine medievalRoleplayEngine;

    private UUID playerUUID = null;
    private String name = "defaultName";
    private String race = "defaultRace";
    private String subculture = "defaultSubculture";
    private int age = 0;
    private String gender = "defaultGender";
    private String religion = "defaultReligion";

    public CharacterCard(MedievalRoleplayEngine medievalRoleplayEngine, UUID uuid) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
        playerUUID = uuid;
    }

    public CharacterCard(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    void setPlayerUUID(UUID newUUID) {
        playerUUID = newUUID;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
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
            File saveFolder = new File("./plugins/MedievalRoleplayEngine/");
            if (!saveFolder.exists()) {
                saveFolder.mkdir();
            }
            File saveFile = new File("./plugins/MedievalRoleplayEngine/" + playerUUID + ".txt");
            if (saveFile.createNewFile()) {
                if (medievalRoleplayEngine.isDebugEnabled()) { System.out.println("Save file for character card belonging to " + playerUUID + " created."); }
            } else {
                if (medievalRoleplayEngine.isDebugEnabled()) { System.out.println("Save file for character card belonging to " + playerUUID + " already exists. Altering."); }
            }

            FileWriter saveWriter = new FileWriter("./plugins/MedievalRoleplayEngine/" + playerUUID + ".txt");

            saveWriter.write(playerUUID.toString() + "\n");
            saveWriter.write(name + "\n");
            saveWriter.write(race + "\n");
            saveWriter.write(subculture + "\n");
            saveWriter.write(age + "\n");
            saveWriter.write(gender + "\n");
            saveWriter.write(religion + "\n");

            saveWriter.close();

            return true;

        } catch (IOException e) {
            if (medievalRoleplayEngine.isDebugEnabled()) { System.out.println("An error occurred saving character card belonging to " + playerUUID); }
            e.printStackTrace();
            return false;
        }
    }

    public boolean load(String filename) {
        try {
            File loadFile = new File("./plugins/MedievalRoleplayEngine/" + filename);
            Scanner loadReader = new Scanner(loadFile);

            try {
                if (loadReader.hasNextLine()) {
                    setPlayerUUID(UUID.fromString(loadReader.nextLine()));
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

                return true;
            } catch (IllegalArgumentException e) {
                // a malformed UUID or non-numeric age line; NumberFormatException is an IllegalArgumentException
                loadReader.close(); // release the file so it can be renamed
                File movedFile = moveAside(loadFile);
                if (movedFile != null) {
                    System.out.println("Skipping malformed character card file " + filename + " (moved aside to " + movedFile.getName() + "): " + e.getMessage());
                } else {
                    System.out.println("Skipping malformed character card file " + filename + " (could not be moved aside, so it may be overwritten): " + e.getMessage());
                }
                return false;
            } finally {
                loadReader.close();
            }
        } catch (FileNotFoundException e) {
            if (medievalRoleplayEngine.isDebugEnabled()) { System.out.println("An error occurred loading the file " + filename + "."); }
            e.printStackTrace();
            return false;
        }
    }

    // Renames a malformed save file to <name>.malformed (or <name>.malformed.<n> if that is taken) so that the
    // default card created when the player next joins cannot overwrite it. Returns the new file, or null on failure.
    private File moveAside(File file) {
        File target = new File(file.getPath() + ".malformed");
        for (int i = 1; target.exists(); i++) {
            target = new File(file.getPath() + ".malformed." + i);
        }
        return file.renameTo(target) ? target : null;
    }

    public boolean legacyLoad(String filename) {
        try {
            File loadFile = new File("./plugins/medieval-roleplay-engine/" + filename);
            Scanner loadReader = new Scanner(loadFile);

            try {
                if (loadReader.hasNextLine()) {
                    setPlayerUUID(medievalRoleplayEngine.uuidChecker.findUUIDBasedOnPlayerName(loadReader.nextLine()));
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

                return true;
            } catch (IllegalArgumentException e) {
                // a non-numeric age line; NumberFormatException is an IllegalArgumentException
                System.out.println("Skipping malformed character card file " + filename + ": " + e.getMessage());
                return false;
            } finally {
                loadReader.close();
            }
        } catch (FileNotFoundException e) {
            if (medievalRoleplayEngine.isDebugEnabled()) { System.out.println("An error occurred loading the file " + filename + "."); }
            e.printStackTrace();
            return false;
        }
    }

}
