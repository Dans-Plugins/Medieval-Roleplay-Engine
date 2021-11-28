package dansplugins.rpsystem.managers;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.objects.RPCharacter;
import preponderous.ponder.misc.JsonWriterReader;

import java.util.*;

public class StorageManager {

    private static StorageManager instance;
    private final static String FILE_PATH = "./plugins/MedievalRoleplayEngine/";
    private final static String CHARACTERS_FILE_NAME = "characters.json";
    private JsonWriterReader jsonWriterReader = new JsonWriterReader();

    private StorageManager() {

    }

    public static StorageManager getInstance() {
        if (instance == null) {
            instance = new StorageManager();
        }
        return instance;
    }

    public void save() {
        saveCharacters();
        if (MedievalRoleplayEngine.getInstance().getPonderAPI().getConfigService().hasBeenAltered()) {
            MedievalRoleplayEngine.getInstance().saveConfig();
        }
    }

    public void load() {
        // TODO: if updating from pre-v2.0, load in character cards using the legacy load method
        loadCharacters();
    }

    private void saveCharacters() {
        List<Map<String, String>> characters = new ArrayList<>();
        for (RPCharacter character : PersistentData.getInstance().getCharacters()){
            characters.add(character.save());
        }
        jsonWriterReader.writeOutFiles(characters, CHARACTERS_FILE_NAME);
    }

    private void loadCharacters() {
        PersistentData.getInstance().getCharacters().clear();
        ArrayList<HashMap<String, String>> data = jsonWriterReader.loadDataFromFilename(FILE_PATH + CHARACTERS_FILE_NAME);
        HashSet<RPCharacter> characters = new HashSet<>();
        for (Map<String, String> characterData : data){
            RPCharacter warning = new RPCharacter(characterData);
            characters.add(warning);
        }
        PersistentData.getInstance().setCharacters(characters);
    }

    @Deprecated
    private void saveCardFileNames() {
        /*
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
            for (RPCharacter card : PersistentData.getInstance().getCards()) {
//                System.out.println("[MedievalRoleplayEngine.getInstance().isDebugEnabled()] Saving card with UUID: " + card.getPlayerUUID());
                if (card.getPlayerUUID() != null) {
                    saveWriter.write(card.getPlayerUUID().toString() + ".txt" + "\n");
                }
            }

            saveWriter.close();

        } catch (IOException e) {
            if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("An error occurred while saving character card filenames."); }
        }
        */
    }

    @Deprecated
    private void legacyLoadCards() {
        /*
        try {
            if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("Attempting to load character cards..."); }
            File loadFile = new File("./plugins/MedievalRoleplayEngine/" + "cards.txt");
            Scanner loadReader = new Scanner(loadFile);

            // actual loading
            while (loadReader.hasNextLine()) {
                String nextFilename = loadReader.nextLine();
                RPCharacter temp = new CharacterCard();
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
        */
    }

}
