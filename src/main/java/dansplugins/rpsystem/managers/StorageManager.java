package dansplugins.rpsystem.managers;

import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.objects.CharacterCard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class StorageManager {

    private static StorageManager instance;

    private final boolean debug = false;

    private StorageManager() {

    }

    public static StorageManager getInstance() {
        if (instance == null) {
            instance = new StorageManager();
        }
        return instance;
    }

    public void saveCardFileNames() {
        try {
            File saveFolder = new File("./plugins/MedievalRoleplayEngine/");
            if (!saveFolder.exists()) {
                saveFolder.mkdir();
            }
            File saveFile = new File("./plugins/MedievalRoleplayEngine/" + "cards.txt");
            if (saveFile.createNewFile()) {
                if (debug) { System.out.println("Save file for character card filenames created."); }
            } else {
                if (debug) { System.out.println("Save file for character card filenames already exists. Overwriting."); }
            }

            FileWriter saveWriter = new FileWriter(saveFile);

            // actual saving takes place here
            for (CharacterCard card : PersistentData.getInstance().getCards()) {
//                System.out.println("[DEBUG] Saving card with UUID: " + card.getPlayerUUID());
                if (card.getPlayerUUID() != null) {
                    saveWriter.write(card.getPlayerUUID().toString() + ".txt" + "\n");
                }
            }

            saveWriter.close();

        } catch (IOException e) {
            if (debug) { System.out.println("An error occurred while saving character card filenames."); }
        }
    }

    public void saveCards() {
        for (CharacterCard card : PersistentData.getInstance().getCards()) {
            if (card.getPlayerUUID() != null) {
                card.save();
            }
        }
    }

    public void loadCards() {
        try {
            if (debug) { System.out.println("Attempting to load character cards..."); }
            File loadFile = new File("./plugins/MedievalRoleplayEngine/" + "cards.txt");
            Scanner loadReader = new Scanner(loadFile);

            // actual loading
            while (loadReader.hasNextLine()) {
                String nextFilename = loadReader.nextLine();
                CharacterCard temp = new CharacterCard();
                temp.load(nextFilename);

                // existence check
                int index = -1;
                for (int i = 0; i < PersistentData.getInstance().getCards().size(); i++) {
                    if (PersistentData.getInstance().getCards().get(i).getPlayerUUID().equals(temp.getPlayerUUID())) {
                        index = i;
                    }
                }
                if (index != -1) {
                    PersistentData.getInstance().getCards().remove(index);
                }

                PersistentData.getInstance().getCards().add(temp);

            }

            loadReader.close();
            if (debug) { System.out.println("Character cards successfully loaded."); }
        } catch (FileNotFoundException e) {
            if (debug) { System.out.println("Error loading the character cards!"); }
        }
    }

    public void legacyLoadCards() {
        try {
            if (debug) { System.out.println("Attempting to load character cards..."); }
            File loadFile = new File("./plugins/medieval-roleplay-engine/" + "card-player-names.txt");
            Scanner loadReader = new Scanner(loadFile);

            // actual loading
            while (loadReader.hasNextLine()) {
                String nextName = loadReader.nextLine();
                CharacterCard temp = new CharacterCard();
                temp.legacyLoad(nextName + ".txt");

                PersistentData.getInstance().getCards().add(temp);

            }

            loadReader.close();

            if (debug) { System.out.println("Character cards successfully loaded."); }
        } catch (FileNotFoundException e) {
            if (debug) { System.out.println("Error loading the character cards!"); }
        }
    }

    // Recursive file delete from https://www.baeldung.com/java-delete-directory
    public boolean deleteLegacyFiles(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteLegacyFiles(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    public boolean oldSaveFolderPresent() {
        File saveFolder = new File("./plugins/medieval-roleplay-engine/");
        return saveFolder.exists();
    }

}
