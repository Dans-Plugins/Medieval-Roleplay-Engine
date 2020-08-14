package rpsystem.Subsystems;

import rpsystem.Objects.CharacterCard;
import rpsystem.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;

import static rpsystem.Subsystems.UtilitySubsystem.findUUIDBasedOnPlayerName;

public class StorageSubsystem {

    Main main = null;

    public StorageSubsystem(Main plugin) {
        main = plugin;
    }

    public void saveCardFileNames() {
        try {
            File saveFolder = new File("./plugins/MedievalRoleplayEngine/");
            if (!saveFolder.exists()) {
                saveFolder.mkdir();
            }
            File saveFile = new File("./plugins/MedievalRoleplayEngine/" + "card-player-names.txt");
            if (saveFile.createNewFile()) {
                System.out.println("Save file for character card filenames created.");
            } else {
                System.out.println("Save file for character card filenames already exists. Overwriting.");
            }

            FileWriter saveWriter = new FileWriter(saveFile);

            // actual saving takes place here
            for (CharacterCard card : main.cards) {
                saveWriter.write(card.getPlayerUUID().toString() + "\n");
            }

            saveWriter.close();

        } catch (IOException e) {
            System.out.println("An error occurred while saving character card filenames.");
        }
    }

    public void saveCards() {
        for (CharacterCard card : main.cards) {
            card.save();
        }
    }

    public void loadCards() {
        try {
            System.out.println("Attempting to load character cards...");
            File loadFile = new File("./plugins/MedievalRoleplayEngine/" + "card-player-names.txt");
            Scanner loadReader = new Scanner(loadFile);

            // actual loading
            while (loadReader.hasNextLine()) {
                String nextName = loadReader.nextLine();
                CharacterCard temp = new CharacterCard(UUID.fromString(nextName));
                temp.load(nextName + ".txt");

                // existence check
                boolean exists = false;
                for (int i = 0; i < main.cards.size(); i++) {
                    if (main.cards.get(i).getName().equalsIgnoreCase(temp.getName())) {
                        main.cards.remove(i);
                    }
                }

                main.cards.add(temp);

            }

            loadReader.close();
            System.out.println("Character cards successfully loaded.");
        } catch (FileNotFoundException e) {
            System.out.println("Error loading the character cards!");
            e.printStackTrace();
        }
    }

    public void legacyLoadCards() {
        try {
            System.out.println("Attempting to load character cards...");
            File loadFile = new File("./plugins/medieval-roleplay-engine/" + "card-player-names.txt");
            Scanner loadReader = new Scanner(loadFile);

            // actual loading
            while (loadReader.hasNextLine()) {
                String nextName = loadReader.nextLine();
                CharacterCard temp = new CharacterCard(findUUIDBasedOnPlayerName(nextName));
                temp.legacyLoad(nextName + ".txt");

                // existence check
                int index = -1;
                for (int i = 0; i < main.cards.size(); i++) {
                    if (main.cards.get(i).getName().equalsIgnoreCase(temp.getName())) {
                        index = i;
                    }
                }
                if (index != -1) {
                    main.cards.remove(index);
                }

                main.cards.add(temp);

            }

            loadReader.close();
            System.out.println("Character cards successfully loaded.");
        } catch (FileNotFoundException e) {
            System.out.println("Error loading the character cards!");
            e.printStackTrace();
        }
    }

    public boolean oldSaveFolderPresent() {
        File saveFolder = new File("./plugins/medieval-roleplay-engine/");
        return saveFolder.exists();
    }

}
