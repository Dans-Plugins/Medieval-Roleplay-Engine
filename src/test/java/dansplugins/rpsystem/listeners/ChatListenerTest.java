package dansplugins.rpsystem.listeners;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.cards.CardRepository;
import dansplugins.rpsystem.cards.CharacterCard;
import dansplugins.rpsystem.config.ConfigService;
import dansplugins.rpsystem.ephemeral.EphemeralData;
import dansplugins.rpsystem.utils.ColorChecker;
import dansplugins.rpsystem.utils.Messenger;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Characterizes how {@link ChatListener} turns normal chat into local roleplay chat, as described under "Using Roleplay
 * Chat" and "Emoting Inline While in Local Chat" in {@code USER_GUIDE.md}: which messages it takes over, the hidden
 * local chat refusal, the {@code CharacterName: "message"} format and the asterisk emote split.
 *
 * <p>{@link MedievalRoleplayEngine} is a {@code JavaPlugin}, whose constructor refuses to run outside a server's plugin
 * class loader, so the plugin is allocated without running any constructor and only what {@link ChatListener} reads is
 * filled in: {@code configService}, {@code ephemeralData}, {@code colorChecker}, {@code cardRepository},
 * {@code messenger} and {@code JavaPlugin}'s loaded config. The local and emote radii and colors are given distinct
 * values so that every broadcast shows which of the two it used.</p>
 */
public class ChatListenerTest {
    private static final ChatColor NEGATIVE = ChatColor.RED;
    private static final ChatColor LOCAL = ChatColor.GRAY;
    private static final ChatColor EMOTE = ChatColor.DARK_PURPLE;
    private static final int LOCAL_RADIUS = 25;
    private static final int EMOTE_RADIUS = 10;
    private static final String CHARACTER = "Aldric of Wessex";

    private final StubConfigService configService = new StubConfigService();
    private final EphemeralData ephemeralData = new EphemeralData();
    private final RecordingMessenger messenger = new RecordingMessenger();
    private final List<String> received = new ArrayList<>();
    private final UUID uuid = UUID.randomUUID();

    private ChatListener listener;
    private Player player;

    @Before
    public void setUp() throws Exception {
        YamlConfiguration config = new YamlConfiguration();
        config.set("localChatRadius", LOCAL_RADIUS);
        config.set("emoteRadius", EMOTE_RADIUS);
        config.set("localChatColor", "gray");
        config.set("emoteColor", "dark_purple");
        config.set("debugMode", false);

        CardRepository cardRepository = new CardRepository();
        CharacterCard card = new CharacterCard(null, uuid);
        card.setName(CHARACTER);
        cardRepository.getCards().add(card);

        listener = new ChatListener(pluginWith(config, cardRepository));
        player = player();
        ephemeralData.getPlayersSpeakingInLocalChat().add(uuid);
    }

    @Test
    public void withChatFeaturesDisabledChatIsLeftToTheServer() {
        configService.chatFeaturesEnabled = false;

        AsyncPlayerChatEvent event = chat("Well met.");

        assertFalse(event.isCancelled());
        assertTrue(messenger.broadcasts.isEmpty());
        assertTrue(received.isEmpty());
    }

    @Test
    public void aPlayerNotInLocalChatIsLeftToTheServer() {
        ephemeralData.getPlayersSpeakingInLocalChat().clear();

        AsyncPlayerChatEvent event = chat("Well met.");

        assertFalse(event.isCancelled());
        assertTrue(messenger.broadcasts.isEmpty());
        assertTrue(received.isEmpty());
    }

    @Test
    public void aPlayerWhoHidLocalChatIsToldHowToShowItAndTheirMessageGoesNowhere() {
        ephemeralData.getPlayersWhoHaveHiddenLocalChat().add(uuid);

        AsyncPlayerChatEvent event = chat("Well met.");

        assertTrue(event.isCancelled());
        assertEquals(Collections.singletonList(
                NEGATIVE + "You have hidden local chat. Type '/rp show' to talk in local chat."), received);
        assertTrue(messenger.broadcasts.isEmpty());
    }

    @Test
    public void aPlainMessageIsSpokenAsTheCharacterWithinTheLocalRadiusAndKeptOutOfServerChat() {
        AsyncPlayerChatEvent event = chat("Well met, traveller.");

        assertTrue(event.isCancelled());
        assertEquals(Collections.singletonList(
                speech(CHARACTER + ": \"Well met, traveller.\"")), messenger.broadcasts);
        assertTrue(received.isEmpty());
    }

    @Test
    public void anEmoteIsSplitOutAndSentAfterTheSpokenRemainderWithinTheEmoteRadius() {
        AsyncPlayerChatEvent event = chat("*draws his sword* Stand back!");

        assertTrue(event.isCancelled());
        assertEquals(Arrays.asList(
                speech(CHARACTER + ": \"Stand back!\""),
                emote(CHARACTER + " draws his sword")), messenger.broadcasts);
    }

    @Test
    public void theSpokenRemainderIsTrimmedButInnerSpacingIsKept() {
        chat("Hold, *raises a hand* stranger.  ");

        assertEquals(Arrays.asList(
                speech(CHARACTER + ": \"Hold,  stranger.\""),
                emote(CHARACTER + " raises a hand")), messenger.broadcasts);
    }

    @Test
    public void whenOnlyAnEmoteIsTypedOnlyTheEmoteIsSent() {
        AsyncPlayerChatEvent event = chat("  *nods slowly*  ");

        assertTrue(event.isCancelled());
        assertEquals(Collections.singletonList(emote(CHARACTER + " nods slowly")), messenger.broadcasts);
    }

    @Test
    public void onlyTheFirstPairOfAsterisksIsAnEmoteAndLaterOnesAreSpokenAsTyped() {
        chat("*nods* Aye. *smiles*");

        assertEquals(Arrays.asList(
                speech(CHARACTER + ": \"Aye. *smiles*\""),
                emote(CHARACTER + " nods")), messenger.broadcasts);
    }

    @Test
    public void everyRepeatOfTheFirstEmoteIsRemovedFromTheSpokenRemainder() {
        chat("*nods* Aye. *nods*");

        assertEquals(Arrays.asList(
                speech(CHARACTER + ": \"Aye.\""),
                emote(CHARACTER + " nods")), messenger.broadcasts);
    }

    @Test
    public void aLoneAsteriskIsNotAnEmoteAndIsSpokenAsTyped() {
        AsyncPlayerChatEvent event = chat("Five * five is twenty-five.");

        assertTrue(event.isCancelled());
        assertEquals(Collections.singletonList(
                speech(CHARACTER + ": \"Five * five is twenty-five.\"")), messenger.broadcasts);
    }

    @Test
    public void anEmptyPairOfAsterisksSendsNoEmoteAndOnlyTheSpokenRemainder() {
        chat("** Hello");

        assertEquals(Collections.singletonList(speech(CHARACTER + ": \"Hello\"")), messenger.broadcasts);
    }

    @Test
    public void aPairOfAsterisksAroundOnlySpacesSendsNoEmote() {
        chat("*   * Hello");

        assertEquals(Collections.singletonList(speech(CHARACTER + ": \"Hello\"")), messenger.broadcasts);
    }

    @Test
    public void anEmptyPairOfAsterisksAloneSendsNothingAndIsKeptOutOfServerChat() {
        AsyncPlayerChatEvent event = chat("**");

        assertEquals(Collections.emptyList(), messenger.broadcasts);
        assertTrue(event.isCancelled());
    }

    private AsyncPlayerChatEvent chat(String message) {
        AsyncPlayerChatEvent event =
                new AsyncPlayerChatEvent(true, player, message, new HashSet<>(Collections.singletonList(player)));
        listener.handle(event);
        return event;
    }

    private Broadcast speech(String text) {
        return new Broadcast(player, LOCAL + text, LOCAL_RADIUS);
    }

    private Broadcast emote(String text) {
        return new Broadcast(player, EMOTE + "" + ChatColor.ITALIC + text, EMOTE_RADIUS);
    }

    private Player player() {
        return (Player) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class<?>[]{Player.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        switch (method.getName()) {
                            case "getUniqueId":
                                return uuid;
                            case "sendMessage":
                                received.add((String) args[0]);
                                return null;
                            case "hashCode":
                                return System.identityHashCode(proxy);
                            case "equals":
                                return proxy == args[0];
                            case "toString":
                                return CHARACTER;
                            default:
                                throw new AssertionError("Unexpected call to Player." + method.getName());
                        }
                    }
                });
    }

    /** Allocates the plugin without running {@code JavaPlugin}'s constructor, then sets what {@link ChatListener} reads. */
    private MedievalRoleplayEngine pluginWith(YamlConfiguration config, CardRepository cardRepository)
            throws Exception {
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Object unsafe = theUnsafe.get(null);
        Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
        MedievalRoleplayEngine plugin =
                (MedievalRoleplayEngine) allocateInstance.invoke(unsafe, MedievalRoleplayEngine.class);
        setField(MedievalRoleplayEngine.class, plugin, "configService", configService);
        setField(MedievalRoleplayEngine.class, plugin, "ephemeralData", ephemeralData);
        setField(MedievalRoleplayEngine.class, plugin, "colorChecker", new FixedColorChecker());
        setField(MedievalRoleplayEngine.class, plugin, "cardRepository", cardRepository);
        setField(MedievalRoleplayEngine.class, plugin, "messenger", messenger);
        setField(JavaPlugin.class, plugin, "newConfig", config);
        return plugin;
    }

    private static void setField(Class<?> owner, Object target, String name, Object value) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    /** Answers {@code chatFeaturesEnabled} without a {@code config.yml}. */
    private static final class StubConfigService extends ConfigService {
        private boolean chatFeaturesEnabled = true;

        StubConfigService() {
            super(null);
        }

        @Override
        public boolean getBoolean(String option) {
            if (!"chatFeaturesEnabled".equals(option)) {
                throw new AssertionError("Unexpected boolean option " + option);
            }
            return chatFeaturesEnabled;
        }
    }

    /** Resolves color names as configured, with a fixed negative alert color so no {@code config.yml} is needed. */
    private static final class FixedColorChecker extends ColorChecker {
        FixedColorChecker() {
            super(null);
        }

        @Override
        public ChatColor getNegativeAlertColor() {
            return NEGATIVE;
        }
    }

    /** Records each broadcast instead of looking for nearby players. */
    private static final class RecordingMessenger extends Messenger {
        private final List<Broadcast> broadcasts = new ArrayList<>();

        RecordingMessenger() {
            super(null);
        }

        @Override
        public int sendRPMessageToPlayersWithinDistance(Player sender, String message, int distance) {
            broadcasts.add(new Broadcast(sender, message, distance));
            return 0;
        }
    }

    private static final class Broadcast {
        private final Player sender;
        private final String message;
        private final int distance;

        Broadcast(Player sender, String message, int distance) {
            this.sender = sender;
            this.message = message;
            this.distance = distance;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Broadcast)) {
                return false;
            }
            Broadcast that = (Broadcast) other;
            return sender == that.sender && message.equals(that.message) && distance == that.distance;
        }

        @Override
        public int hashCode() {
            return message.hashCode() * 31 + distance;
        }

        @Override
        public String toString() {
            return "Broadcast(" + message + ", " + distance + ")";
        }
    }
}
