package dansplugins.rpsystem.commands.emote;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Covers {@code /emote} (alias {@code /me}) as documented in {@code COMMANDS.md}: which permission nodes it accepts and
 * how an action is sent. Every path that has answered the player returns {@code true}, because a {@code false} return
 * makes Bukkit send the command's usage line as well.
 *
 * <p>{@link MedievalRoleplayEngine} is a {@code JavaPlugin}, whose constructor refuses to run outside a server's plugin
 * class loader, so the plugin is allocated without running any constructor and only what {@link EmoteCommand} reads is
 * filled in.</p>
 */
public class EmoteCommandTest {
    private static final ChatColor NEGATIVE = ChatColor.RED;
    private static final int EMOTE_RADIUS = 10;
    private static final String CHARACTER = "Aldric of Wessex";
    private static final UUID PLAYER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final RecordingMessenger messenger = new RecordingMessenger();
    private final List<String> received = new ArrayList<>();
    private final Set<String> permissions = new HashSet<>();

    private EmoteCommand emoteCommand;
    private Player player;

    @Before
    public void setUp() throws Exception {
        emoteCommand = new EmoteCommand(plugin());
        player = player();
    }

    @Test
    public void aSenderThatIsNotAPlayerIsIgnored() {
        assertFalse(emoteCommand.emoteAction(console(), new String[]{"bows"}));

        assertTrue(messenger.broadcasts.isEmpty());
    }

    @Test
    public void emoteMeAndDefaultEachGrantTheCommand() {
        for (String node : Arrays.asList("rp.emote", "rp.me", "rp.default")) {
            permissions.clear();
            permissions.add(node);
            messenger.broadcasts.clear();

            assertTrue(node, emoteCommand.emoteAction(player, new String[]{"bows"}));

            assertEquals(node, 1, messenger.broadcasts.size());
        }
        assertTrue(received.isEmpty());
    }

    @Test
    public void theActionIsSentInItalicsAfterTheCharacterNameWithinTheEmoteRadius() {
        permissions.add("rp.emote");

        assertTrue(emoteCommand.emoteAction(player, new String[]{"bows", "gracefully"}));

        assertEquals(Collections.singletonList(
                ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + CHARACTER + " bows gracefully"), messenger.broadcasts);
        assertEquals(Collections.singletonList(EMOTE_RADIUS), messenger.distances);
        assertEquals(Collections.singletonList(player), messenger.senders);
    }

    @Test
    public void withoutAnActionTheUsageIsShownAndNothingIsSent() {
        permissions.add("rp.emote");

        assertTrue(emoteCommand.emoteAction(player, new String[0]));

        assertEquals(Collections.singletonList(NEGATIVE + "Usage: /emote (action)"), received);
        assertTrue(messenger.broadcasts.isEmpty());
    }

    @Test
    public void withoutPermissionThePlayerIsToldAndNothingIsSent() {
        assertTrue(emoteCommand.emoteAction(player, new String[]{"bows"}));

        assertEquals(Collections.singletonList(NEGATIVE + "Sorry! In order to use this command, you need one of the"
                + " following permissions: 'rp.emote', 'rp.me'"), received);
        assertTrue(messenger.broadcasts.isEmpty());
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
    private MedievalRoleplayEngine plugin() throws Exception {
        YamlConfiguration config = new YamlConfiguration();
        config.set("emoteRadius", EMOTE_RADIUS);
        config.set("emoteColor", "dark_purple");

        CardRepository cardRepository = new CardRepository();
        CharacterCard card = new CharacterCard(null, PLAYER_UUID);
        card.setName(CHARACTER);
        cardRepository.getCards().add(card);

        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Object unsafe = theUnsafe.get(null);
        Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
        MedievalRoleplayEngine plugin =
                (MedievalRoleplayEngine) allocateInstance.invoke(unsafe, MedievalRoleplayEngine.class);
        setField(MedievalRoleplayEngine.class, plugin, "colorChecker", new FixedColorChecker());
        setField(MedievalRoleplayEngine.class, plugin, "argumentParser", new ArgumentParser());
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
        private final List<Player> senders = new ArrayList<>();
        private final List<String> broadcasts = new ArrayList<>();
        private final List<Integer> distances = new ArrayList<>();

        RecordingMessenger() {
            super(null);
        }

        @Override
        public int sendRPMessageToPlayersWithinDistance(Player sender, String message, int distance) {
            senders.add(sender);
            broadcasts.add(message);
            distances.add(distance);
            return 0;
        }
    }
}
