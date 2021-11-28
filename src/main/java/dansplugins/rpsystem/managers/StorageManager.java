package dansplugins.rpsystem.managers;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.objects.RPCharacter;
import dansplugins.rpsystem.objects.deprecated.CharacterCard;
import dansplugins.rpsystem.utils.Logger;
import preponderous.ponder.misc.JsonWriterReader;

import java.io.File;
import java.io.FileNotFoundException;
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
        if (MedievalRoleplayEngine.getInstance().isVersionMismatched() && MedievalRoleplayEngine.getInstance().getOldVersion().charAt(1) != '2') {
            // load in character cards using the legacy load method
            legacyLoadCards();
            Logger.getInstance().log("Loaded legacy files.");
            return;
        }
        loadCharacters();
        Logger.getInstance().log("Loaded files.");
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
    private void legacyLoadCards() {
        try {
            if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("Attempting to load character cards..."); }
            File loadFile = new File("./plugins/MedievalRoleplayEngine/" + "cards.txt");
            Scanner loadReader = new Scanner(loadFile);

            // actual loading
            while (loadReader.hasNextLine()) {
                String nextFilename = loadReader.nextLine();
                CharacterCard temp = new CharacterCard();
                temp.load(nextFilename);
                RPCharacter character = convertCardToCharacter(temp);
                PersistentData.getInstance().getCharacters().add(character);
            }

            loadReader.close();
            if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("Character cards successfully loaded."); }
        } catch (FileNotFoundException e) {
            if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("Error loading the character cards!"); }
        }
    }

    private RPCharacter convertCardToCharacter(CharacterCard card) {
        RPCharacter toReturn = new RPCharacter(card.getPlayerUUID());
        toReturn.setInfo("Name", card.getName());
        toReturn.setInfo("Gender", card.getGender());
        toReturn.setInfo("Race", card.getRace());
        toReturn.setInfo("Religion", card.getReligion());
        toReturn.setInfo("Subculture", card.getSubculture());
        return toReturn;
    }

}
