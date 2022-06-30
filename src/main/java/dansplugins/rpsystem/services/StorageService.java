package dansplugins.rpsystem.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.objects.RPCharacter;
import dansplugins.rpsystem.objects.deprecated.CharacterCard;
import dansplugins.rpsystem.utils.Logger;
import preponderous.ponder.minecraft.bukkit.tools.UUIDChecker;

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
    private static Type LIST_MAP_TYPE = (new TypeToken<ArrayList<HashMap<String, String>>>() {}).getType();

    public StorageService(ConfigService configService, MedievalRoleplayEngine medievalRoleplayEngine, Logger logger, PersistentData persistentData) {
        this.configService = configService;
        this.medievalRoleplayEngine = medievalRoleplayEngine;
        this.logger = logger;
        this.persistentData = persistentData;
    }

    public void save() {
        saveCharacters();
        if (configService.hasBeenAltered()) {
            medievalRoleplayEngine.saveConfig();
        }
    }

    public void load() {
        logger.log("Version mismatched: " + medievalRoleplayEngine.isVersionMismatched());
        logger.log("Old version: " + medievalRoleplayEngine.getOldVersion());
        loadCharacters();
        if (medievalRoleplayEngine.isVersionMismatched() && medievalRoleplayEngine.getOldVersion().charAt(1) != '2') {
            // load in character cards using the legacy load method
            legacyLoadCards();
        }
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

    @Deprecated
    private void legacyLoadCards() {
        try {
            if (medievalRoleplayEngine.isDebugEnabled()) { System.out.println("Attempting to load character cards..."); }
            File loadFile = new File("./plugins/MedievalRoleplayEngine/" + "cards.txt");
            Scanner loadReader = new Scanner(loadFile);

            // actual loading
            while (loadReader.hasNextLine()) {
                String nextFilename = loadReader.nextLine();
                CharacterCard temp = new CharacterCard(medievalRoleplayEngine);
                temp.load(nextFilename);
                RPCharacter character = convertCardToCharacter(temp);
                boolean success = persistentData.getCharacters().add(character);
                if (!success) {
                    UUIDChecker uuidChecker = new UUIDChecker();
                    logger.log("Character card for player " + uuidChecker.findPlayerNameBasedOnUUID(character.getPlayerUUID()) + " is already present. Not overwriting.");
                }
            }

            loadReader.close();
            if (medievalRoleplayEngine.isDebugEnabled()) { System.out.println("Character cards successfully loaded."); }
        } catch (FileNotFoundException e) {
            if (medievalRoleplayEngine.isDebugEnabled()) { System.out.println("Error loading the character cards!"); }
        }
        logger.log("Loaded legacy files.");
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