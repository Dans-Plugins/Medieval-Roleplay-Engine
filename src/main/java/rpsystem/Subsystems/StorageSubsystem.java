package rpsystem.Subsystems;

import rpsystem.CharacterCard;
import rpsystem.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class StorageSubsystem {

    Main main = null;

    public StorageSubsystem(Main plugin) {
        main = plugin;
    }

    public void saveCardFileNames() {
        try {
            File saveFolder = new File("./plugins/medieval-roleplay-engine/");
            if (!saveFolder.exists()) {
                saveFolder.mkdir();
            }
            File saveFile = new File("./plugins/medieval-roleplay-engine/" + "card-player-names.txt");
            if (saveFile.createNewFile()) {
                System.out.println("Save file for character card filenames created.");
            } else {
                System.out.println("Save file for character card filenames already exists. Overwriting.");
            }

            FileWriter saveWriter = new FileWriter(saveFile);

            // actual saving takes place here
            for (CharacterCard card : main.cards) {
                saveWriter.write(card.getPlayerName() + "\n");
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
            File loadFile = new File("./plugins/medieval-roleplay-engine/" + "card-player-names.txt");
            Scanner loadReader = new Scanner(loadFile);

            // actual loading
            while (loadReader.hasNextLine()) {
                String nextName = loadReader.nextLine();
                CharacterCard temp = new CharacterCard(nextName);
                temp.load(nextName + ".txt"); // provides owner field among other things

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

}
