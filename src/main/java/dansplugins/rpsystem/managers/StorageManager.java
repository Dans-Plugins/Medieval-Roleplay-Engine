package dansplugins.rpsystem.managers;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.objects.old.CharacterCard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class StorageManager {

    private static StorageManager instance;

    private StorageManager() {

    }

    public static StorageManager getInstance() {
        if (instance == null) {
            instance = new StorageManager();
        }
        return instance;
    }

    public void save() {
        StorageManager.getInstance().saveCardFileNames();
        StorageManager.getInstance().saveCards();
        if (MedievalRoleplayEngine.getInstance().getPonderAPI().getConfigService().hasBeenAltered()) {
            MedievalRoleplayEngine.getInstance().saveConfig();
        }
    }

    public void load() {
        loadCards();
    }

    private void saveCardFileNames() {
        try {
            File saveFolder = new File("./plugins/MedievalRoleplayEngine/");
            if (!saveFolder.exists()) {
                saveFolder.mkdir();
            }
            File saveFile = new File("./plugins/MedievalRoleplayEngine/" + "cards.txt");
            if (saveFile.createNewFile()) {
                if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("Save file for character card filenames created."); }
            } else {
                if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("Save file for character card filenames already exists. Overwriting."); }
            }

            FileWriter saveWriter = new FileWriter(saveFile);

            // actual saving takes place here
            for (CharacterCard card : PersistentData.getInstance().getCards()) {
//                System.out.println("[MedievalRoleplayEngine.getInstance().isDebugEnabled()] Saving card with UUID: " + card.getPlayerUUID());
                if (card.getPlayerUUID() != null) {
                    saveWriter.write(card.getPlayerUUID().toString() + ".txt" + "\n");
                }
            }

            saveWriter.close();

        } catch (IOException e) {
            if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("An error occurred while saving character card filenames."); }
        }
    }

    private void saveCards() {
        for (CharacterCard card : PersistentData.getInstance().getCards()) {
            if (card.getPlayerUUID() != null) {
                card.save();
            }
        }
    }

    private void loadCards() {
        try {
            if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("Attempting to load character cards..."); }
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
            if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("Character cards successfully loaded."); }
        } catch (FileNotFoundException e) {
            if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("Error loading the character cards!"); }
        }
    }

}
