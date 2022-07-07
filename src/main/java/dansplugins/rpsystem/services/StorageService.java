package dansplugins.rpsystem.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.objects.RPCharacter;
import dansplugins.rpsystem.utils.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author Daniel McCoy Stephenson
 */
public class StorageService {
    private final ConfigService configService;
    private final MedievalRoleplayEngine medievalRoleplayEngine;
    private final Logger logger;
    private final PersistentData persistentData;

    private final static String FILE_PATH = "./plugins/MedievalRoleplayEngine/";
    private final static String CHARACTERS_FILE_NAME = "characters.json";
    private final Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
    private static final Type LIST_MAP_TYPE = (new TypeToken<ArrayList<HashMap<String, String>>>() {}).getType();

    public StorageService(ConfigService configService, MedievalRoleplayEngine medievalRoleplayEngine, Logger logger, PersistentData persistentData) {
        this.configService = configService;
        this.medievalRoleplayEngine = medievalRoleplayEngine;
        this.logger = logger;
        this.persistentData = persistentData;
    }

    public void save() {
        saveCharacters();
    }

    public void load() {
        logger.log("Version mismatched: " + medievalRoleplayEngine.isVersionMismatched());
        logger.log("Old version: " + medievalRoleplayEngine.getOldVersion());
        loadCharacters();
    }

    private void saveCharacters() {
        List<Map<String, String>> characters = new ArrayList<>();
        for (RPCharacter character : persistentData.getCharacters()){
            characters.add(character.save());
        }
        writeOutFiles(characters, CHARACTERS_FILE_NAME);
    }

    private void loadCharacters() {
        persistentData.getCharacters().clear();
        ArrayList<HashMap<String, String>> data = loadDataFromFilename(FILE_PATH + CHARACTERS_FILE_NAME);
        HashSet<RPCharacter> characters = new HashSet<>();
        for (Map<String, String> characterData : data){
            RPCharacter warning = new RPCharacter(characterData);
            characters.add(warning);
        }
        persistentData.setCharacters(characters);
        logger.log("Loaded files.");
    }

    private void writeOutFiles(List<Map<String, String>> saveData, String fileName) {
        try {
            File parentFolder = new File(FILE_PATH);
            parentFolder.mkdir();
            File file = new File(FILE_PATH, fileName);
            file.createNewFile();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            outputStreamWriter.write(this.gson.toJson(saveData));
            outputStreamWriter.close();
        } catch (IOException ignored) {
        }
    }

    private ArrayList<HashMap<String, String>> loadDataFromFilename(String filename) {
        try {
            Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
            JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8));
            return gson.fromJson(reader, LIST_MAP_TYPE);
        } catch (FileNotFoundException var4) {
            return new ArrayList<>();
        }
    }
}