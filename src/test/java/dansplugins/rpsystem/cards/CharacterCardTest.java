package dansplugins.rpsystem.cards;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Characterizes a {@link CharacterCard}'s defaults and how {@link CharacterCard#load} reads the per-player save
 * file that {@link CharacterCard#save} writes: one value per line, in the order UUID, name, race, subculture, age,
 * gender, religion. {@code load} only consults the plugin when the file is missing, so no plugin instance is needed
 * here. Save files are written to {@code ./plugins/MedievalRoleplayEngine/}, the same relative folder the plugin
 * uses, and removed again after each test.
 */
public class CharacterCardTest {
    private static final File SAVE_FOLDER = new File("./plugins/MedievalRoleplayEngine/");

    private boolean createdPluginsFolder;
    private boolean createdSaveFolder;
    private File saveFile;

    @Before
    public void createSaveFolder() {
        File pluginsFolder = SAVE_FOLDER.getParentFile();
        createdPluginsFolder = !pluginsFolder.exists();
        createdSaveFolder = !SAVE_FOLDER.exists();
        assertTrue(SAVE_FOLDER.isDirectory() || SAVE_FOLDER.mkdirs());
        saveFile = new File(SAVE_FOLDER, "character-card-test-" + UUID.randomUUID() + ".txt");
    }

    @After
    public void removeSaveFolder() {
        // Best effort: when load() throws it leaves its Scanner open, which blocks the delete on Windows until exit.
        if (saveFile.exists() && !saveFile.delete()) {
            saveFile.deleteOnExit();
            return;
        }
        if (createdSaveFolder) {
            SAVE_FOLDER.delete();
        }
        if (createdPluginsFolder) {
            SAVE_FOLDER.getParentFile().delete();
        }
    }

    @Test
    public void aNewCardHasThePlaceholderDefaults() {
        CharacterCard card = new CharacterCard(null);

        assertNull(card.getPlayerUUID());
        assertEquals("defaultName", card.getName());
        assertEquals("defaultRace", card.getRace());
        assertEquals("defaultSubculture", card.getSubculture());
        assertEquals(0, card.getAge());
        assertEquals("defaultGender", card.getGender());
        assertEquals("defaultReligion", card.getReligion());
    }

    @Test
    public void theUUIDConstructorSetsOnlyThePlayerUUID() {
        UUID uuid = UUID.randomUUID();
        CharacterCard card = new CharacterCard(null, uuid);

        assertEquals(uuid, card.getPlayerUUID());
        assertEquals("defaultName", card.getName());
        assertEquals(0, card.getAge());
    }

    @Test
    public void settersReplaceEachField() {
        CharacterCard card = new CharacterCard(null);
        card.setName("Sir Roderick");
        card.setRace("Human");
        card.setSubculture("Northman");
        card.setAge(42);
        card.setGender("Male");
        card.setReligion("The Old Gods");

        assertEquals("Sir Roderick", card.getName());
        assertEquals("Human", card.getRace());
        assertEquals("Northman", card.getSubculture());
        assertEquals(42, card.getAge());
        assertEquals("Male", card.getGender());
        assertEquals("The Old Gods", card.getReligion());
    }

    @Test
    public void loadReadsEveryFieldInSaveOrder() throws IOException {
        UUID uuid = UUID.randomUUID();
        writeSaveFile(uuid.toString(), "Ser Brienne of Tarth", "Human", "Stormlander", "31", "Female", "The Seven");

        CharacterCard card = new CharacterCard(null);
        assertTrue(card.load(saveFile.getName()));

        assertEquals(uuid, card.getPlayerUUID());
        assertEquals("Ser Brienne of Tarth", card.getName());
        assertEquals("Human", card.getRace());
        assertEquals("Stormlander", card.getSubculture());
        assertEquals(31, card.getAge());
        assertEquals("Female", card.getGender());
        assertEquals("The Seven", card.getReligion());
    }

    @Test
    public void loadOfATruncatedFileKeepsTheDefaultsForMissingLines() throws IOException {
        UUID uuid = UUID.randomUUID();
        writeSaveFile(uuid.toString(), "Aldric");

        CharacterCard card = new CharacterCard(null);
        assertTrue(card.load(saveFile.getName()));

        assertEquals(uuid, card.getPlayerUUID());
        assertEquals("Aldric", card.getName());
        assertEquals("defaultRace", card.getRace());
        assertEquals(0, card.getAge());
        assertEquals("defaultReligion", card.getReligion());
    }

    @Test
    public void loadOfAnEmptyFileLeavesTheUUIDUnset() throws IOException {
        // StorageService.loadCards skips a card whose UUID is still null after loading.
        writeSaveFile();

        CharacterCard card = new CharacterCard(null);
        assertTrue(card.load(saveFile.getName()));

        assertNull(card.getPlayerUUID());
        assertEquals("defaultName", card.getName());
    }

    @Test
    public void loadIgnoresLinesAfterTheReligion() throws IOException {
        UUID uuid = UUID.randomUUID();
        writeSaveFile(uuid.toString(), "Aldric", "Elf", "Sylvan", "120", "Male", "Nature", "unexpected extra line");

        CharacterCard card = new CharacterCard(null);
        assertTrue(card.load(saveFile.getName()));

        assertEquals("Nature", card.getReligion());
    }

    @Test(expected = NumberFormatException.class)
    public void loadOfANonNumericAgeThrows() throws IOException {
        // Only FileNotFoundException is caught, so a hand-edited age escapes load() and StorageService.loadCards.
        writeSaveFile(UUID.randomUUID().toString(), "Aldric", "Elf", "Sylvan", "old", "Male", "Nature");

        new CharacterCard(null).load(saveFile.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void loadOfAMalformedUUIDThrows() throws IOException {
        // As with the age, a malformed UUID line is not caught by load() or StorageService.loadCards.
        writeSaveFile("not-a-uuid", "Aldric");

        new CharacterCard(null).load(saveFile.getName());
    }

    private void writeSaveFile(String... lines) throws IOException {
        FileWriter writer = new FileWriter(saveFile);
        try {
            for (String line : lines) {
                writer.write(line + "\n");
            }
        } finally {
            writer.close();
        }
    }
}
