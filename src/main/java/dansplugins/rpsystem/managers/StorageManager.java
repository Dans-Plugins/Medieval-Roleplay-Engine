package dansplugins.rpsystem.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.objects.RPCharacter;
import dansplugins.rpsystem.objects.deprecated.CharacterCard;
import dansplugins.rpsystem.utils.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class StorageManager {

    private static StorageManager instance;
    private final static String FILE_PATH = "./plugins/MedievalRoleplayEngine/";
    private final static String CHARACTERS_FILE_NAME = "characters.json";
    private final Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
    private static Type LIST_MAP_TYPE = (new TypeToken<ArrayList<HashMap<String, String>>>() {}).getType();

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
        Logger.getInstance().log("Version mismatched: " + MedievalRoleplayEngine.getInstance().isVersionMismatched());
        Logger.getInstance().log("Old version: " + MedievalRoleplayEngine.getInstance().getOldVersion());
        loadCharacters();
        if (MedievalRoleplayEngine.getInstance().isVersionMismatched() && MedievalRoleplayEngine.getInstance().getOldVersion().charAt(1) != '2') {
            // load in character cards using the legacy load method
            legacyLoadCards();
        }
    }

    private void saveCharacters() {
        List<Map<String, String>> characters = new ArrayList<>();
        for (RPCharacter character : PersistentData.getInstance().getCharacters()){
            characters.add(character.save());
        }
        writeOutFiles(characters, CHARACTERS_FILE_NAME);
    }

    private void loadCharacters() {
        PersistentData.getInstance().getCharacters().clear();
        ArrayList<HashMap<String, String>> data = loadDataFromFilename(FILE_PATH + CHARACTERS_FILE_NAME);
        HashSet<RPCharacter> characters = new HashSet<>();
        for (Map<String, String> characterData : data){
            RPCharacter warning = new RPCharacter(characterData);
            characters.add(warning);
        }
        PersistentData.getInstance().setCharacters(characters);
        Logger.getInstance().log("Loaded files.");
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
                boolean success = PersistentData.getInstance().getCharacters().add(character);
                if (!success) {
                    Logger.getInstance().log("Character card for player " + MedievalRoleplayEngine.getInstance().getToolbox().getUUIDChecker().findPlayerNameBasedOnUUID(character.getPlayerUUID()) + " is already present. Not overwriting.");
                }
            }

            loadReader.close();
            if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("Character cards successfully loaded."); }
        } catch (FileNotFoundException e) {
            if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("Error loading the character cards!"); }
        }
        Logger.getInstance().log("Loaded legacy files.");
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

    private boolean writeOutFiles(List<Map<String, String>> saveData, String fileName) {
        try {
            File parentFolder = new File(FILE_PATH);
            parentFolder.mkdir();
            File file = new File(FILE_PATH, fileName);
            file.createNewFile();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            outputStreamWriter.write(this.gson.toJson(saveData));
            outputStreamWriter.close();
            return true;
        } catch (IOException var6) {
            return false;
        }
    }

    private ArrayList<HashMap<String, String>> loadDataFromFilename(String filename) {
        try {
            Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
            JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8));
            return (ArrayList)gson.fromJson(reader, LIST_MAP_TYPE);
        } catch (FileNotFoundException var4) {
            return new ArrayList();
        }
    }

}
