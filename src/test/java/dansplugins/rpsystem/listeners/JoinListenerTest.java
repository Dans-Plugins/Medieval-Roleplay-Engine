package dansplugins.rpsystem.listeners;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.cards.CardRepository;
import dansplugins.rpsystem.cards.CharacterCard;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Characterizes how {@link JoinListener} gives a joining player a character card, as described under "Getting
 * Started" in {@code USER_GUIDE.md}: a card with placeholder values is created the first time a player joins, and a
 * player who already has one keeps it.
 *
 * <p>{@link MedievalRoleplayEngine} is a {@code JavaPlugin}, whose constructor refuses to run outside a server's plugin
 * class loader, so the plugin is allocated without running any constructor and only {@code cardRepository}, the one
 * field {@link JoinListener} reads, is filled in.</p>
 */
public class JoinListenerTest {
    private static final UUID PLAYER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID OTHER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    private final CardRepository cardRepository = new CardRepository();

    private JoinListener listener;
    private Player player;

    @Before
    public void setUp() throws Exception {
        listener = new JoinListener(plugin());
        player = player(PLAYER_UUID);
    }

    @Test
    public void aFirstJoinCreatesACardWithThePlaceholderValues() {
        join(player);

        assertEquals(1, cardRepository.getCards().size());
        CharacterCard card = cardRepository.getCard(PLAYER_UUID);
        assertEquals(PLAYER_UUID, card.getPlayerUUID());
        assertEquals("defaultName", card.getName());
        assertEquals("defaultRace", card.getRace());
        assertEquals("defaultSubculture", card.getSubculture());
        assertEquals("defaultGender", card.getGender());
        assertEquals("defaultReligion", card.getReligion());
        assertEquals(0, card.getAge());
    }

    @Test
    public void aPlayerWhoAlreadyHasACardKeepsItAndGetsNoSecondOne() {
        CharacterCard existing = new CharacterCard(null, PLAYER_UUID);
        existing.setName("Aldric of Wessex");
        cardRepository.getCards().add(existing);

        join(player);

        assertEquals(1, cardRepository.getCards().size());
        assertSame(existing, cardRepository.getCard(PLAYER_UUID));
        assertEquals("Aldric of Wessex", existing.getName());
    }

    @Test
    public void joiningAgainAfterTheFirstJoinDoesNotAddASecondCard() {
        join(player);
        CharacterCard first = cardRepository.getCard(PLAYER_UUID);

        join(player);

        assertEquals(1, cardRepository.getCards().size());
        assertSame(first, cardRepository.getCard(PLAYER_UUID));
    }

    @Test
    public void anotherPlayersCardDoesNotCountAsTheJoiningPlayersCard() {
        cardRepository.getCards().add(new CharacterCard(null, OTHER_UUID));

        join(player);

        assertEquals(2, cardRepository.getCards().size());
        assertEquals(PLAYER_UUID, cardRepository.getCard(PLAYER_UUID).getPlayerUUID());
    }

    private void join(Player joining) {
        listener.handle(new PlayerJoinEvent(joining, "joined"));
    }

    /** A player answering only its UUID; any other call fails the test. */
    private static Player player(final UUID uuid) {
        return (Player) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class<?>[]{Player.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        switch (method.getName()) {
                            case "getUniqueId":
                                return uuid;
                            case "hashCode":
                                return System.identityHashCode(proxy);
                            case "equals":
                                return proxy == args[0];
                            case "toString":
                                return "player";
                            default:
                                throw new AssertionError("Unexpected call to Player." + method.getName());
                        }
                    }
                });
    }

    /** Allocates the plugin without running {@code JavaPlugin}'s constructor, then sets what the listener reads. */
    private MedievalRoleplayEngine plugin() throws Exception {
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Object unsafe = theUnsafe.get(null);
        Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
        MedievalRoleplayEngine plugin =
                (MedievalRoleplayEngine) allocateInstance.invoke(unsafe, MedievalRoleplayEngine.class);
        Field field = MedievalRoleplayEngine.class.getDeclaredField("cardRepository");
        field.setAccessible(true);
        field.set(plugin, cardRepository);
        return plugin;
    }
}
