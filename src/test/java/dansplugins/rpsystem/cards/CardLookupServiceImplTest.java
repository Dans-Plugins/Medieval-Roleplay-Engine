package dansplugins.rpsystem.cards;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.utils.Logger;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Characterizes how {@link CardLookupServiceImpl} finds a player's card. {@code /card} (with or without a player name)
 * looks cards up through this service rather than through {@link CardRepository} directly.
 */
public class CardLookupServiceImplTest {
    private final CardRepository cardRepository = new CardRepository();
    private final List<String> logged = new ArrayList<>();
    private CardLookupServiceImpl lookupService;

    @Before
    public void setUp() throws Exception {
        lookupService = new CardLookupServiceImpl(plugin());
    }

    @Test
    public void aCardInTheRepositoryIsReturnedAndReportedAsFoundInStorage() {
        UUID uuid = UUID.randomUUID();
        CharacterCard card = new CharacterCard(null, uuid);
        cardRepository.getCards().add(new CharacterCard(null, UUID.randomUUID()));
        cardRepository.getCards().add(card);

        assertSame(card, lookupService.lookup(uuid));
        assertEquals(Arrays.asList("Looking up character card for " + uuid, "Found in storage!"), logged);
    }

    @Test
    public void aSecondLookupIsAnsweredFromTheCache() {
        UUID uuid = UUID.randomUUID();
        CharacterCard card = new CharacterCard(null, uuid);
        cardRepository.getCards().add(card);
        lookupService.lookup(uuid);
        logged.clear();

        assertSame(card, lookupService.lookup(uuid));
        assertEquals(Arrays.asList("Looking up character card for " + uuid, "Found in cache!"), logged);
    }

    @Test
    public void anEqualButDistinctUUIDInstanceIsFoundInTheCache() {
        UUID uuid = UUID.randomUUID();
        CharacterCard card = new CharacterCard(null, uuid);
        cardRepository.getCards().add(card);
        lookupService.lookup(uuid);
        logged.clear();

        assertSame(card, lookupService.lookup(UUID.fromString(uuid.toString())));
        assertEquals("Found in cache!", logged.get(1));
    }

    @Test
    public void anUnknownPlayerGetsNullAndIsReportedAsNotFound() {
        cardRepository.getCards().add(new CharacterCard(null, UUID.randomUUID()));
        UUID unknown = UUID.randomUUID();

        assertNull(lookupService.lookup(unknown));
        assertEquals(Arrays.asList("Looking up character card for " + unknown, "Not found."), logged);
    }

    @Test
    public void aMissIsNotCachedSoACardAddedLaterIsFound() {
        UUID uuid = UUID.randomUUID();
        assertNull(lookupService.lookup(uuid));

        CharacterCard card = new CharacterCard(null, uuid);
        cardRepository.getCards().add(card);
        logged.clear();

        assertSame(card, lookupService.lookup(uuid));
        assertEquals("Found in storage!", logged.get(1));
    }

    @Test
    public void aCachedCardIsStillReturnedAfterTheRepositoryReplacesIt() {
        // The cache is never invalidated. The only code that removes a card from the repository is
        // StorageService.loadCards, which runs at startup before any lookup, so this is not reached in practice.
        UUID uuid = UUID.randomUUID();
        CharacterCard original = new CharacterCard(null, uuid);
        cardRepository.getCards().add(original);
        lookupService.lookup(uuid);

        cardRepository.getCards().clear();
        cardRepository.getCards().add(new CharacterCard(null, uuid));

        assertSame(original, lookupService.lookup(uuid));
    }

    @Test
    public void changesToACachedCardAreVisibleThroughLaterLookups() {
        UUID uuid = UUID.randomUUID();
        CharacterCard card = new CharacterCard(null, uuid);
        cardRepository.getCards().add(card);
        lookupService.lookup(uuid);

        card.setName("Aldric");

        assertEquals("Aldric", lookupService.lookup(uuid).getName());
    }

    @Test
    public void eachPlayersCardIsCachedSeparately() {
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        CharacterCard firstCard = new CharacterCard(null, first);
        CharacterCard secondCard = new CharacterCard(null, second);
        cardRepository.getCards().add(firstCard);
        cardRepository.getCards().add(secondCard);
        lookupService.lookup(first);
        lookupService.lookup(second);
        logged.clear();

        assertSame(firstCard, lookupService.lookup(first));
        assertSame(secondCard, lookupService.lookup(second));
        assertEquals(2, Collections.frequency(logged, "Found in cache!"));
    }

    /** Allocates the plugin without running {@code JavaPlugin}'s constructor, then sets what the service reads. */
    private MedievalRoleplayEngine plugin() throws Exception {
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Object unsafe = theUnsafe.get(null);
        Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
        MedievalRoleplayEngine plugin =
                (MedievalRoleplayEngine) allocateInstance.invoke(unsafe, MedievalRoleplayEngine.class);
        setField(plugin, "cardRepository", cardRepository);
        setField(plugin, "logger", new RecordingLogger(logged));
        return plugin;
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = MedievalRoleplayEngine.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    /** Records each message instead of consulting {@code debugMode}, which would need a {@code config.yml}. */
    private static final class RecordingLogger extends Logger {
        private final List<String> messages;

        RecordingLogger(List<String> messages) {
            super(null);
            this.messages = messages;
        }

        @Override
        public void log(String message) {
            messages.add(message);
        }
    }
}
