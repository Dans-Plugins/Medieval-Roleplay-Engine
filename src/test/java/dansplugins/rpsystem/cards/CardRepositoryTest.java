package dansplugins.rpsystem.cards;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Characterizes how {@link CardRepository} finds a player's card. The listeners, the chat commands and the
 * PlaceholderAPI expansion all look cards up through {@link CardRepository#getCard} and {@link CardRepository#hasCard}.
 */
public class CardRepositoryTest {
    private final CardRepository cardRepository = new CardRepository();

    @Test
    public void aNewRepositoryHasNoCards() {
        assertTrue(cardRepository.getCards().isEmpty());
        assertNull(cardRepository.getCard(UUID.randomUUID()));
        assertFalse(cardRepository.hasCard(UUID.randomUUID()));
    }

    @Test
    public void aCardIsFoundByItsPlayersUUID() {
        UUID uuid = UUID.randomUUID();
        CharacterCard card = new CharacterCard(null, uuid);
        cardRepository.getCards().add(new CharacterCard(null, UUID.randomUUID()));
        cardRepository.getCards().add(card);

        assertSame(card, cardRepository.getCard(uuid));
        assertTrue(cardRepository.hasCard(uuid));
    }

    @Test
    public void anEqualButDistinctUUIDInstanceStillMatches() {
        UUID uuid = UUID.randomUUID();
        CharacterCard card = new CharacterCard(null, uuid);
        cardRepository.getCards().add(card);

        UUID copy = UUID.fromString(uuid.toString());
        assertSame(card, cardRepository.getCard(copy));
        assertTrue(cardRepository.hasCard(copy));
    }

    @Test
    public void anUnknownUUIDIsNotFound() {
        cardRepository.getCards().add(new CharacterCard(null, UUID.randomUUID()));

        UUID unknown = UUID.randomUUID();
        assertNull(cardRepository.getCard(unknown));
        assertFalse(cardRepository.hasCard(unknown));
    }

    @Test
    public void whenTwoCardsShareAUUIDTheFirstAddedIsReturned() {
        // StorageService.loadCards removes an existing card before adding a reloaded one, so this is not expected
        // in practice, but the lookup itself returns the earliest match.
        UUID uuid = UUID.randomUUID();
        CharacterCard first = new CharacterCard(null, uuid);
        cardRepository.getCards().add(first);
        cardRepository.getCards().add(new CharacterCard(null, uuid));

        assertSame(first, cardRepository.getCard(uuid));
    }

    @Test
    public void aNullUUIDNeverMatchesEvenACardWithoutAUUID() {
        cardRepository.getCards().add(new CharacterCard(null));

        assertNull(cardRepository.getCard(null));
        assertFalse(cardRepository.hasCard(null));
    }

    @Test
    public void aCardWithoutAUUIDIsSkippedRatherThanThrowing() {
        UUID uuid = UUID.randomUUID();
        CharacterCard card = new CharacterCard(null, uuid);
        cardRepository.getCards().add(new CharacterCard(null));
        cardRepository.getCards().add(card);

        assertSame(card, cardRepository.getCard(uuid));
        assertTrue(cardRepository.hasCard(uuid));
    }
}
