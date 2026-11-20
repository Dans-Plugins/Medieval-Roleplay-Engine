package dansplugins.rpsystem.storage;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.cards.CardRepository;
import dansplugins.rpsystem.cards.CharacterCard;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Characterizes how {@link StorageService} persists character cards between restarts: {@code cards.txt} in
 * {@code ./plugins/MedievalRoleplayEngine/} lists one {@code <uuid>.txt} file name per line, and each of those files
 * holds one card in the format {@link CharacterCard#save} writes. {@code onEnable} calls {@link
 * StorageService#loadCards} and {@code onDisable} calls {@link StorageService#saveCardFileNames} followed by {@link
 * StorageService#saveCards}.
 *
 * <p>The service only reads {@code cardRepository} and {@code debugMode} from the plugin, so the plugin is allocated
 * without running {@code JavaPlugin}'s constructor and just those are filled in. Files are written to the same relative
 * folders the plugin uses; any {@code cards.txt} already there is restored, and every other file a test creates is
 * removed again afterwards.</p>
 */
public class StorageServiceTest {
    private static final File SAVE_FOLDER = new File("./plugins/MedievalRoleplayEngine/");
    private static final File CARDS_FILE = new File(SAVE_FOLDER, "cards.txt");
    private static final File LEGACY_FOLDER = new File("./plugins/medieval-roleplay-engine/");

    private final CardRepository cardRepository = new CardRepository();
    private final List<File> createdFiles = new ArrayList<>();

    private MedievalRoleplayEngine plugin;
    private StorageService storageService;
    private boolean createdPluginsFolder;
    private boolean createdSaveFolder;
    private byte[] originalCardsFile;

    @Before
    public void setUp() throws Exception {
        createdPluginsFolder = !SAVE_FOLDER.getParentFile().exists();
        createdSaveFolder = !SAVE_FOLDER.exists();
        assertTrue(SAVE_FOLDER.isDirectory() || SAVE_FOLDER.mkdirs());
        originalCardsFile = CARDS_FILE.exists() ? Files.readAllBytes(CARDS_FILE.toPath()) : null;
        plugin = plugin();
        storageService = new StorageService(plugin);
    }

    @After
    public void tearDown() throws IOException {
        for (File file : createdFiles) {
            if (file.exists() && !file.delete()) {
                file.deleteOnExit();
            }
        }
        if (originalCardsFile != null) {
            Files.write(CARDS_FILE.toPath(), originalCardsFile);
        } else if (CARDS_FILE.exists() && !CARDS_FILE.delete()) {
            CARDS_FILE.deleteOnExit();
        }
        if (createdSaveFolder) {
            SAVE_FOLDER.delete();
        }
        if (createdPluginsFolder) {
            SAVE_FOLDER.getParentFile().delete();
        }
    }

    @Test
    public void saveCardFileNamesListsOneFileNamePerCardInRepositoryOrder() throws IOException {
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        cardRepository.getCards().add(new CharacterCard(null, first));
        cardRepository.getCards().add(new CharacterCard(null, second));

        storageService.saveCardFileNames();

        assertEquals(Arrays.asList(first + ".txt", second + ".txt"), readLines(CARDS_FILE));
    }

    @Test
    public void saveCardFileNamesLeavesOutACardWithoutAUUID() throws IOException {
        UUID uuid = UUID.randomUUID();
        cardRepository.getCards().add(new CharacterCard(null));
        cardRepository.getCards().add(new CharacterCard(null, uuid));

        storageService.saveCardFileNames();

        assertEquals(Collections.singletonList(uuid + ".txt"), readLines(CARDS_FILE));
    }

    @Test
    public void saveCardFileNamesOverwritesThePreviousList() throws IOException {
        writeFile(CARDS_FILE, "stale-entry.txt", "another-stale-entry.txt");
        UUID uuid = UUID.randomUUID();
        cardRepository.getCards().add(new CharacterCard(null, uuid));

        storageService.saveCardFileNames();

        assertEquals(Collections.singletonList(uuid + ".txt"), readLines(CARDS_FILE));
    }

    @Test
    public void saveCardFileNamesWithNoCardsWritesAnEmptyList() throws IOException {
        writeFile(CARDS_FILE, "stale-entry.txt");

        storageService.saveCardFileNames();

        assertTrue(CARDS_FILE.isFile());
        assertTrue(readLines(CARDS_FILE).isEmpty());
    }

    @Test
    public void saveCardsWritesOneFilePerCardWithAUUID() throws IOException {
        UUID uuid = UUID.randomUUID();
        CharacterCard card = new CharacterCard(plugin, uuid);
        card.setName("Aldric of Wessex");
        card.setAge(42);
        cardRepository.getCards().add(card);
        cardRepository.getCards().add(new CharacterCard(plugin));
        File cardFile = trackedCardFile(uuid);
        File nullFile = new File(SAVE_FOLDER, "null.txt");
        boolean nullFileExisted = nullFile.exists();

        storageService.saveCards();

        assertEquals(Arrays.asList(uuid.toString(), "Aldric of Wessex", "defaultRace", "defaultSubculture", "42",
                "defaultGender", "defaultReligion"), readLines(cardFile));
        assertEquals(nullFileExisted, nullFile.exists());
    }

    @Test
    public void savingLeavesNoTemporaryFilesInTheSaveFolder() {
        UUID uuid = UUID.randomUUID();
        cardRepository.getCards().add(new CharacterCard(plugin, uuid));
        trackedCardFile(uuid);
        createdFiles.add(new File(SAVE_FOLDER, uuid + ".txt.tmp"));
        createdFiles.add(new File(SAVE_FOLDER, "cards.txt.tmp"));

        storageService.saveCardFileNames();
        storageService.saveCards();

        assertFalse(new File(SAVE_FOLDER, "cards.txt.tmp").exists());
        assertFalse(new File(SAVE_FOLDER, uuid + ".txt.tmp").exists());
    }

    @Test
    public void cardsSavedOnDisableAreLoadedBackOnEnable() {
        UUID uuid = UUID.randomUUID();
        CharacterCard card = new CharacterCard(plugin, uuid);
        card.setName("Ser Brienne of Tarth");
        card.setRace("Human");
        card.setSubculture("Stormlander");
        card.setAge(31);
        card.setGender("Female");
        card.setReligion("The Seven");
        cardRepository.getCards().add(card);
        trackedCardFile(uuid);
        storageService.saveCardFileNames();
        storageService.saveCards();

        cardRepository.getCards().clear();
        storageService.loadCards();

        assertEquals(1, cardRepository.getCards().size());
        CharacterCard loaded = cardRepository.getCard(uuid);
        assertNotSame(card, loaded);
        assertEquals("Ser Brienne of Tarth", loaded.getName());
        assertEquals("Human", loaded.getRace());
        assertEquals("Stormlander", loaded.getSubculture());
        assertEquals(31, loaded.getAge());
        assertEquals("Female", loaded.getGender());
        assertEquals("The Seven", loaded.getReligion());
    }

    @Test
    public void loadCardsReplacesACardAlreadyInTheRepositoryAndAppendsTheLoadedOne() throws IOException {
        UUID reloaded = UUID.randomUUID();
        UUID untouched = UUID.randomUUID();
        CharacterCard stale = new CharacterCard(null, reloaded);
        stale.setName("Stale");
        CharacterCard other = new CharacterCard(null, untouched);
        cardRepository.getCards().add(stale);
        cardRepository.getCards().add(other);
        writeCardFile(reloaded, "Fresh");
        writeFile(CARDS_FILE, reloaded + ".txt");

        storageService.loadCards();

        assertEquals(2, cardRepository.getCards().size());
        assertSame(other, cardRepository.getCards().get(0));
        assertEquals(reloaded, cardRepository.getCards().get(1).getPlayerUUID());
        assertEquals("Fresh", cardRepository.getCards().get(1).getName());
    }

    @Test
    public void loadCardsSkipsUnreadableEntriesAndKeepsLoadingTheRest() throws IOException {
        UUID malformed = UUID.randomUUID();
        UUID good = UUID.randomUUID();
        writeFile(trackedCardFile(malformed), malformed.toString(), "Aldric", "Elf", "Sylvan", "old");
        trackedFile(new File(SAVE_FOLDER, malformed + ".txt.malformed"));
        File empty = trackedFile(new File(SAVE_FOLDER, "storage-service-test-" + UUID.randomUUID() + ".txt"));
        writeFile(empty);
        writeCardFile(good, "Godric");
        writeFile(CARDS_FILE, "missing-" + UUID.randomUUID() + ".txt", malformed + ".txt", empty.getName(),
                good + ".txt");

        storageService.loadCards();

        assertEquals(1, cardRepository.getCards().size());
        assertEquals("Godric", cardRepository.getCard(good).getName());
    }

    @Test
    public void loadCardsWithoutACardsFileLeavesTheRepositoryUnchanged() {
        assertTrue(!CARDS_FILE.exists() || CARDS_FILE.delete());
        CharacterCard existing = new CharacterCard(null, UUID.randomUUID());
        cardRepository.getCards().add(existing);

        storageService.loadCards();

        assertEquals(1, cardRepository.getCards().size());
        assertSame(existing, cardRepository.getCards().get(0));
    }

    @Test
    public void deleteLegacyFilesRemovesTheWholeFolderTree() throws IOException {
        File root = Files.createTempDirectory("storage-service-test").toFile();
        File nested = new File(root, "nested");
        assertTrue(nested.mkdir());
        writeFile(new File(root, "card-player-names.txt"), "Aldric");
        writeFile(new File(nested, "Aldric.txt"), "Aldric");

        assertTrue(storageService.deleteLegacyFiles(root));

        assertFalse(root.exists());
    }

    @Test
    public void deleteLegacyFilesOfAMissingFolderReturnsFalse() {
        assertFalse(storageService.deleteLegacyFiles(new File(SAVE_FOLDER, "missing-" + UUID.randomUUID())));
    }

    @Test
    public void oldSaveFolderPresentReflectsTheLegacyFolder() {
        boolean legacyFolderExisted = LEGACY_FOLDER.exists();
        try {
            assertTrue(LEGACY_FOLDER.isDirectory() || LEGACY_FOLDER.mkdirs());
            assertTrue(storageService.oldSaveFolderPresent());
            if (!legacyFolderExisted) {
                assertTrue(LEGACY_FOLDER.delete());
                assertFalse(storageService.oldSaveFolderPresent());
            }
        } finally {
            if (!legacyFolderExisted) {
                LEGACY_FOLDER.delete();
            }
        }
    }

    private void writeCardFile(UUID uuid, String name) throws IOException {
        writeFile(trackedCardFile(uuid), uuid.toString(), name);
    }

    private File trackedCardFile(UUID uuid) {
        return trackedFile(new File(SAVE_FOLDER, uuid + ".txt"));
    }

    private File trackedFile(File file) {
        createdFiles.add(file);
        return file;
    }

    private static List<String> readLines(File file) throws IOException {
        return Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
    }

    private static void writeFile(File file, String... lines) throws IOException {
        FileWriter writer = new FileWriter(file);
        try {
            for (String line : lines) {
                writer.write(line + "\n");
            }
        } finally {
            writer.close();
        }
    }

    /** Allocates the plugin without running {@code JavaPlugin}'s constructor, then sets what the service reads. */
    private MedievalRoleplayEngine plugin() throws Exception {
        YamlConfiguration config = new YamlConfiguration();
        config.set("debugMode", false);

        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Object unsafe = theUnsafe.get(null);
        Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
        MedievalRoleplayEngine plugin =
                (MedievalRoleplayEngine) allocateInstance.invoke(unsafe, MedievalRoleplayEngine.class);
        setField(MedievalRoleplayEngine.class, plugin, "cardRepository", cardRepository);
        setField(JavaPlugin.class, plugin, "newConfig", config);
        return plugin;
    }

    private static void setField(Class<?> owner, Object target, String name, Object value) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
