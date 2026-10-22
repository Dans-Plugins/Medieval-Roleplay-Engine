package dansplugins.rpsystem.utils;

import dansplugins.rpsystem.BukkitTestServer;
import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.config.ConfigService;
import dansplugins.rpsystem.ephemeral.EphemeralData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Characterizes how {@link Messenger} delivers local chat (the radius-based RP and OOC channels used by local chat,
 * emotes, {@code /whisper}, {@code /yell}, {@code /roll} and {@code /bird}): who is in range, which players are skipped,
 * the recipient count callers report, and the {@code logChat} console line.
 *
 * <p>{@link MedievalRoleplayEngine} is a {@code JavaPlugin}, whose constructor refuses to run outside a server's plugin
 * class loader, so the plugin is allocated without running any constructor and only what {@link Messenger} reads is
 * filled in: {@code configService}, {@code ephemeralData} and {@code JavaPlugin}'s logger. Online players come from
 * {@link BukkitTestServer}.</p>
 */
public class MessengerTest {
    private static final String MESSAGE = "Well met, traveller.";
    private static final String LOG_PREFIX = "[MedievalRoleplayEngine] ";

    private final StubConfigService configService = new StubConfigService();
    private final EphemeralData ephemeralData = new EphemeralData();
    private final List<String> logged = new ArrayList<>();
    private final World overworld = world("world");
    private final World nether = world("world_nether");

    private Messenger messenger;

    @Before
    public void setUp() throws Exception {
        PluginLogger logger = new PluginLogger(loggingOwner());
        logger.setUseParentHandlers(false);
        logger.addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                logged.add(record.getMessage());
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        });
        messenger = new Messenger(pluginWith(configService, ephemeralData, logger));
    }

    @After
    public void clearOnlinePlayers() {
        BukkitTestServer.setOnlinePlayers();
    }

    @Test
    public void everyoneInTheSameWorldWithinTheDistanceHearsItIncludingTheSender() {
        RecordingPlayer sender = new RecordingPlayer("Aldric", overworld, 0, 64, 0);
        RecordingPlayer near = new RecordingPlayer("Brena", overworld, 5, 64, 5);
        BukkitTestServer.setOnlinePlayers(sender.player, near.player);

        assertEquals(2, messenger.sendRPMessageToPlayersWithinDistance(sender.player, MESSAGE, 25));

        assertEquals(Collections.singletonList(MESSAGE), sender.received);
        assertEquals(Collections.singletonList(MESSAGE), near.received);
    }

    @Test
    public void aPlayerExactlyAtTheDistanceIsOutOfRangeAndOneJustInsideIsIn() {
        RecordingPlayer sender = new RecordingPlayer("Aldric", overworld, 0, 64, 0);
        RecordingPlayer atEdge = new RecordingPlayer("Brena", overworld, 25, 64, 0);
        RecordingPlayer justInside = new RecordingPlayer("Cedric", overworld, 24.9, 64, 0);
        BukkitTestServer.setOnlinePlayers(sender.player, atEdge.player, justInside.player);

        assertEquals(2, messenger.sendRPMessageToPlayersWithinDistance(sender.player, MESSAGE, 25));

        assertTrue(atEdge.received.isEmpty());
        assertEquals(Collections.singletonList(MESSAGE), justInside.received);
    }

    @Test
    public void theDistanceIsMeasuredInThreeDimensions() {
        RecordingPlayer sender = new RecordingPlayer("Aldric", overworld, 0, 64, 0);
        RecordingPlayer above = new RecordingPlayer("Brena", overworld, 0, 94, 0);
        BukkitTestServer.setOnlinePlayers(sender.player, above.player);

        assertEquals(1, messenger.sendRPMessageToPlayersWithinDistance(sender.player, MESSAGE, 25));

        assertTrue(above.received.isEmpty());
    }

    @Test
    public void playersInAnotherWorldDoNotHearItHoweverCloseTheirCoordinates() {
        RecordingPlayer sender = new RecordingPlayer("Aldric", overworld, 0, 64, 0);
        RecordingPlayer elsewhere = new RecordingPlayer("Brena", nether, 0, 64, 0);
        BukkitTestServer.setOnlinePlayers(sender.player, elsewhere.player);

        assertEquals(1, messenger.sendRPMessageToPlayersWithinDistance(sender.player, MESSAGE, 25));

        assertTrue(elsewhere.received.isEmpty());
    }

    @Test
    public void aSenderWithNoWorldReachesNobodyNotEvenThemselves() {
        RecordingPlayer sender = new RecordingPlayer("Aldric", null, 0, 64, 0);
        RecordingPlayer near = new RecordingPlayer("Brena", overworld, 0, 64, 0);
        BukkitTestServer.setOnlinePlayers(sender.player, near.player);

        assertEquals(0, messenger.sendRPMessageToPlayersWithinDistance(sender.player, MESSAGE, 25));

        assertTrue(sender.received.isEmpty());
        assertTrue(near.received.isEmpty());
    }

    @Test
    public void aRecipientWithNoWorldIsSkippedWithoutStoppingDelivery() {
        RecordingPlayer sender = new RecordingPlayer("Aldric", overworld, 0, 64, 0);
        RecordingPlayer nowhere = new RecordingPlayer("Brena", null, 0, 64, 0);
        RecordingPlayer near = new RecordingPlayer("Cedric", overworld, 1, 64, 0);
        BukkitTestServer.setOnlinePlayers(sender.player, nowhere.player, near.player);

        assertEquals(2, messenger.sendRPMessageToPlayersWithinDistance(sender.player, MESSAGE, 25));

        assertTrue(nowhere.received.isEmpty());
        assertEquals(Collections.singletonList(MESSAGE), near.received);
    }

    @Test
    public void theExcludingVariantLeavesOutOnlyTheSender() {
        RecordingPlayer sender = new RecordingPlayer("Aldric", overworld, 0, 64, 0);
        RecordingPlayer near = new RecordingPlayer("Brena", overworld, 5, 64, 0);
        BukkitTestServer.setOnlinePlayers(sender.player, near.player);

        assertEquals(1, messenger.sendRPMessageToPlayersWithinDistanceExcludingTarget(sender.player, MESSAGE, 25));

        assertTrue(sender.received.isEmpty());
        assertEquals(Collections.singletonList(MESSAGE), near.received);
    }

    @Test
    public void playersWhoHidLocalChatMissRpMessagesButStillGetOocOnes() {
        RecordingPlayer sender = new RecordingPlayer("Aldric", overworld, 0, 64, 0);
        RecordingPlayer hider = new RecordingPlayer("Brena", overworld, 5, 64, 0);
        BukkitTestServer.setOnlinePlayers(sender.player, hider.player);
        ephemeralData.getPlayersWhoHaveHiddenLocalChat().add(hider.uuid);

        assertEquals(1, messenger.sendRPMessageToPlayersWithinDistance(sender.player, "rp", 25));
        assertEquals(0, messenger.sendRPMessageToPlayersWithinDistanceExcludingTarget(sender.player, "rp-excl", 25));
        assertEquals(2, messenger.sendOOCMessageToPlayersWithinDistance(sender.player, "ooc", 25));

        assertEquals(Collections.singletonList("ooc"), hider.received);
    }

    @Test
    public void playersWhoHidLocalOocChatMissOocMessagesButStillGetRpOnes() {
        RecordingPlayer sender = new RecordingPlayer("Aldric", overworld, 0, 64, 0);
        RecordingPlayer hider = new RecordingPlayer("Brena", overworld, 5, 64, 0);
        BukkitTestServer.setOnlinePlayers(sender.player, hider.player);
        ephemeralData.getPlayersWhoHaveHiddenLocalOOCChat().add(hider.uuid);

        assertEquals(1, messenger.sendOOCMessageToPlayersWithinDistance(sender.player, "ooc", 25));
        assertEquals(2, messenger.sendRPMessageToPlayersWithinDistance(sender.player, "rp", 25));

        assertEquals(Collections.singletonList("rp"), hider.received);
    }

    @Test
    public void aSenderWhoHidTheChannelDoesNotSeeTheirOwnMessage() {
        RecordingPlayer sender = new RecordingPlayer("Aldric", overworld, 0, 64, 0);
        BukkitTestServer.setOnlinePlayers(sender.player);
        ephemeralData.getPlayersWhoHaveHiddenLocalChat().add(sender.uuid);

        assertEquals(0, messenger.sendRPMessageToPlayersWithinDistance(sender.player, MESSAGE, 25));

        assertTrue(sender.received.isEmpty());
    }

    @Test
    public void nothingIsLoggedWhileLogChatIsOff() {
        RecordingPlayer sender = new RecordingPlayer("Aldric", overworld, 0, 64, 0);
        BukkitTestServer.setOnlinePlayers(sender.player);

        messenger.sendRPMessageToPlayersWithinDistance(sender.player, MESSAGE, 25);
        messenger.sendRPMessageToPlayersWithinDistanceExcludingTarget(sender.player, MESSAGE, 25);
        messenger.sendOOCMessageToPlayersWithinDistance(sender.player, MESSAGE, 25);

        assertTrue(logged.isEmpty());
        assertEquals(Collections.nCopies(3, "logChat"), configService.booleansRead);
    }

    @Test
    public void withLogChatOnEachMessageIsLoggedOnceWithItsChannelAndTheSendersDisplayName() {
        configService.logChat = true;
        RecordingPlayer sender = new RecordingPlayer("Aldric", overworld, 0, 64, 0);
        RecordingPlayer near = new RecordingPlayer("Brena", overworld, 5, 64, 0);
        BukkitTestServer.setOnlinePlayers(sender.player, near.player);

        messenger.sendRPMessageToPlayersWithinDistance(sender.player, "one", 25);
        messenger.sendRPMessageToPlayersWithinDistanceExcludingTarget(sender.player, "two", 25);
        messenger.sendOOCMessageToPlayersWithinDistance(sender.player, "three", 25);

        List<String> expected = new ArrayList<>();
        expected.add(LOG_PREFIX + "[RP] Sir Aldric: one");
        expected.add(LOG_PREFIX + "[RP] Sir Aldric: two");
        expected.add(LOG_PREFIX + "[OOC] Sir Aldric: three");
        assertEquals(expected, logged);
    }

    @Test
    public void aMessageNobodyHeardIsStillLogged() {
        configService.logChat = true;
        RecordingPlayer sender = new RecordingPlayer("Aldric", null, 0, 64, 0);
        BukkitTestServer.setOnlinePlayers(sender.player);

        assertEquals(0, messenger.sendRPMessageToPlayersWithinDistance(sender.player, MESSAGE, 25));

        assertEquals(Collections.singletonList(LOG_PREFIX + "[RP] Sir Aldric: " + MESSAGE), logged);
    }

    /**
     * The minimum {@link PluginLogger} asks of its plugin: a description without a prefix, so every line is prefixed
     * {@value #LOG_PREFIX}, and a server to parent the logger to.
     */
    private static Plugin loggingOwner() {
        BukkitTestServer.install();
        final PluginDescriptionFile description =
                new PluginDescriptionFile("MedievalRoleplayEngine", "test", MedievalRoleplayEngine.class.getName());
        return (Plugin) Proxy.newProxyInstance(Plugin.class.getClassLoader(), new Class<?>[]{Plugin.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        switch (method.getName()) {
                            case "getDescription":
                                return description;
                            case "getServer":
                                return Bukkit.getServer();
                            default:
                                throw new AssertionError("Unexpected call to Plugin." + method.getName());
                        }
                    }
                });
    }

    private static World world(final String name) {
        return (World) Proxy.newProxyInstance(World.class.getClassLoader(), new Class<?>[]{World.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        switch (method.getName()) {
                            case "getName":
                            case "toString":
                                return name;
                            case "hashCode":
                                return System.identityHashCode(proxy);
                            case "equals":
                                return proxy == args[0];
                            default:
                                throw new AssertionError("Unexpected call to World." + method.getName());
                        }
                    }
                });
    }

    /** Allocates the plugin without running {@code JavaPlugin}'s constructor, then sets what {@link Messenger} reads. */
    private static MedievalRoleplayEngine pluginWith(ConfigService configService, EphemeralData ephemeralData,
                                                     PluginLogger logger) throws Exception {
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Object unsafe = theUnsafe.get(null);
        Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
        MedievalRoleplayEngine plugin =
                (MedievalRoleplayEngine) allocateInstance.invoke(unsafe, MedievalRoleplayEngine.class);
        setField(MedievalRoleplayEngine.class, plugin, "configService", configService);
        setField(MedievalRoleplayEngine.class, plugin, "ephemeralData", ephemeralData);
        setField(JavaPlugin.class, plugin, "logger", logger);
        return plugin;
    }

    private static void setField(Class<?> owner, Object target, String name, Object value) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    /** Answers {@code logChat} without a {@code config.yml} and records every boolean option read. */
    private static final class StubConfigService extends ConfigService {
        private final List<String> booleansRead = new ArrayList<>();
        private boolean logChat;

        StubConfigService() {
            super(null);
        }

        @Override
        public boolean getBoolean(String option) {
            booleansRead.add(option);
            if (!"logChat".equals(option)) {
                throw new AssertionError("Unexpected boolean option " + option);
            }
            return logChat;
        }
    }

    /** An online player at a fixed location whose display name is {@code "Sir " + name}, recording what it is sent. */
    private static final class RecordingPlayer {
        private final UUID uuid = UUID.randomUUID();
        private final List<String> received = new ArrayList<>();
        private final Player player;

        RecordingPlayer(final String name, World world, double x, double y, double z) {
            final Location location = new Location(world, x, y, z);
            player = (Player) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class<?>[]{Player.class},
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) {
                            switch (method.getName()) {
                                case "getLocation":
                                    return location.clone();
                                case "getUniqueId":
                                    return uuid;
                                case "getDisplayName":
                                    return "Sir " + name;
                                case "sendMessage":
                                    received.add((String) args[0]);
                                    return null;
                                case "hashCode":
                                    return System.identityHashCode(proxy);
                                case "equals":
                                    return proxy == args[0];
                                case "toString":
                                    return name;
                                default:
                                    throw new AssertionError("Unexpected call to Player." + method.getName());
                            }
                        }
                    });
        }
    }
}
