package dansplugins.rpsystem.commands.whisper;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.cards.CardRepository;
import dansplugins.rpsystem.cards.CharacterCard;
import dansplugins.rpsystem.utils.ArgumentParser;
import dansplugins.rpsystem.utils.ColorChecker;
import dansplugins.rpsystem.utils.Messenger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
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
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Characterizes {@code /whisper} as documented in {@code COMMANDS.md}: which permission nodes it accepts, the usage
 * message when no text is given, the {@code CharacterName whispers: "message"} format sent within
 * {@code whisperChatRadius} blocks in {@code whisperChatColor}, and the "players heard your whisper" count.
 *
 * <p>{@link MedievalRoleplayEngine} is a {@code JavaPlugin}, whose constructor refuses to run outside a server's
 * plugin class loader, so the plugin is allocated without running any constructor and only what
 * {@link WhisperCommand} reads is filled in: {@code colorChecker}, {@code cardRepository}, {@code argumentParser},
 * {@code messenger} and {@code JavaPlugin}'s loaded config. The radius is given a value distinct from the shipped
 * default so that every broadcast shows it was read from the config.</p>
 */
public class WhisperCommandTest {
    private static final ChatColor NEGATIVE = ChatColor.RED;
    private static final ChatColor NEUTRAL = ChatColor.GOLD;
    private static final int WHISPER_RADIUS = 7;
    private static final String CHARACTER = "Aldric of Wessex";
    private static final UUID PLAYER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final String DENIED = NEGATIVE + "Sorry! In order to use this command, you need the following"
            + " permission: 'rp.whisper'";
    private static final String USAGE = NEGATIVE + "Usage: /whisper (message)";

    private final YamlConfiguration config = new YamlConfiguration();
    private final RecordingMessenger messenger = new RecordingMessenger();
    private final List<String> received = new ArrayList<>();
    private final Set<String> permissions = new HashSet<>();

    private WhisperCommand whisperCommand;
    private Player player;

    @Before
    public void setUp() throws Exception {
        config.set("whisperChatRadius", WHISPER_RADIUS);
        config.set("whisperChatColor", "dark_aqua");

        CardRepository cardRepository = new CardRepository();
        CharacterCard card = new CharacterCard(null, PLAYER_UUID);
        card.setName(CHARACTER);
        cardRepository.getCards().add(card);

        whisperCommand = new WhisperCommand(pluginWith(cardRepository));
        player = player();
    }

    @Test
    public void aSenderThatIsNotAPlayerIsIgnored() {
        whisperCommand.sendQuietMessage(console(), new String[]{"Meet", "me", "tonight."});

        assertTrue(messenger.broadcasts.isEmpty());
    }

    @Test
    public void whisperAndDefaultEachGrantTheCommand() {
        for (String node : Arrays.asList("rp.whisper", "rp.default")) {
            permissions.clear();
            permissions.add(node);
            messenger.broadcasts.clear();

            whisperCommand.sendQuietMessage(player, new String[]{"Hello"});

            assertEquals(node, Collections.singletonList(whisper(CHARACTER + " whispers: \"Hello\"")),
                    messenger.broadcasts);
        }
    }

    @Test
    public void withoutPermissionThePlayerIsToldAndNothingIsSent() {
        for (String[] args : Arrays.asList(new String[0], new String[]{"Hello"})) {
            received.clear();

            whisperCommand.sendQuietMessage(player, args);

            assertEquals(Collections.singletonList(DENIED), received);
        }
        assertTrue(messenger.broadcasts.isEmpty());
    }

    @Test
    public void withoutTextThePlayerIsShownTheUsage() {
        permissions.add("rp.whisper");

        whisperCommand.sendQuietMessage(player, new String[0]);

        assertEquals(Collections.singletonList(USAGE), received);
        assertTrue(messenger.broadcasts.isEmpty());
    }

    @Test
    public void everyWordIsJoinedIntoTheQuotedMessageUnderTheCharacterName() {
        permissions.add("rp.whisper");

        whisperCommand.sendQuietMessage(player, new String[]{"Meet", "me", "at", "the", "tavern", "tonight."});

        assertEquals(Collections.singletonList(
                whisper(CHARACTER + " whispers: \"Meet me at the tavern tonight.\"")), messenger.broadcasts);
    }

    @Test
    public void theSenderIsToldHowManyPlayersHeardTheWhisper() {
        permissions.add("rp.whisper");
        messenger.recipients = 4;

        whisperCommand.sendQuietMessage(player, new String[]{"Hello"});

        assertEquals(Collections.singletonList(NEUTRAL + "4 players heard your whisper."), received);
    }

    @Test
    public void theCountIsReportedEvenWhenNobodyHeard() {
        permissions.add("rp.whisper");
        messenger.recipients = 0;

        whisperCommand.sendQuietMessage(player, new String[]{"Hello"});

        assertEquals(Collections.singletonList(NEUTRAL + "0 players heard your whisper."), received);
    }

    @Test
    public void theRadiusAndColorAreReadFromTheConfigOnEveryUse() {
        permissions.add("rp.whisper");
        config.set("whisperChatRadius", 12);
        config.set("whisperChatColor", "LIGHT_PURPLE");

        whisperCommand.sendQuietMessage(player, new String[]{"Hello"});

        assertEquals(Collections.singletonList(
                new Broadcast(player, ChatColor.LIGHT_PURPLE + CHARACTER + " whispers: \"Hello\"", 12)),
                messenger.broadcasts);
    }

    @Test
    public void anUnrecognizedColorFallsBackToWhite() {
        permissions.add("rp.whisper");
        config.set("whisperChatColor", "Dark_Aqua");

        whisperCommand.sendQuietMessage(player, new String[]{"Hello"});

        assertEquals(Collections.singletonList(
                new Broadcast(player, ChatColor.WHITE + CHARACTER + " whispers: \"Hello\"", WHISPER_RADIUS)),
                messenger.broadcasts);
    }

    private Broadcast whisper(String text) {
        return new Broadcast(player, ChatColor.DARK_AQUA + text, WHISPER_RADIUS);
    }

    private Player player() {
        return (Player) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class<?>[]{Player.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        switch (method.getName()) {
                            case "hasPermission":
                                return permissions.contains((String) args[0]);
                            case "getUniqueId":
                                return PLAYER_UUID;
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

    private static CommandSender console() {
        return (CommandSender) Proxy.newProxyInstance(CommandSender.class.getClassLoader(),
                new Class<?>[]{CommandSender.class}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        throw new AssertionError("The console sender should not be consulted: " + method.getName());
                    }
                });
    }

    /** Allocates the plugin without running {@code JavaPlugin}'s constructor, then sets what the command reads. */
    private MedievalRoleplayEngine pluginWith(CardRepository cardRepository) throws Exception {
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Object unsafe = theUnsafe.get(null);
        Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
        MedievalRoleplayEngine plugin =
                (MedievalRoleplayEngine) allocateInstance.invoke(unsafe, MedievalRoleplayEngine.class);
        setField(MedievalRoleplayEngine.class, plugin, "colorChecker", new FixedColorChecker());
        setField(MedievalRoleplayEngine.class, plugin, "cardRepository", cardRepository);
        setField(MedievalRoleplayEngine.class, plugin, "argumentParser", new ArgumentParser());
        setField(MedievalRoleplayEngine.class, plugin, "messenger", messenger);
        setField(JavaPlugin.class, plugin, "newConfig", config);
        return plugin;
    }

    private static void setField(Class<?> owner, Object target, String name, Object value) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    /** Resolves color names as configured, with fixed alert colors so no {@code config.yml} is needed. */
    private static final class FixedColorChecker extends ColorChecker {
        FixedColorChecker() {
            super(null);
        }

        @Override
        public ChatColor getNeutralAlertColor() {
            return NEUTRAL;
        }

        @Override
        public ChatColor getNegativeAlertColor() {
            return NEGATIVE;
        }
    }

    /** Records each broadcast instead of looking for nearby players, and reports a chosen number of recipients. */
    private static final class RecordingMessenger extends Messenger {
        private final List<Broadcast> broadcasts = new ArrayList<>();
        private int recipients;

        RecordingMessenger() {
            super(null);
        }

        @Override
        public int sendRPMessageToPlayersWithinDistance(Player sender, String message, int distance) {
            broadcasts.add(new Broadcast(sender, message, distance));
            return recipients;
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
