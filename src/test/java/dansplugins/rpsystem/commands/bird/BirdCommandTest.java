package dansplugins.rpsystem.commands.bird;

import dansplugins.rpsystem.BukkitTestServer;
import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.config.ConfigService;
import dansplugins.rpsystem.ephemeral.EphemeralData;
import dansplugins.rpsystem.utils.ArgumentParser;
import dansplugins.rpsystem.utils.ColorChecker;
import dansplugins.rpsystem.utils.Messenger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.After;
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
 * Covers {@code /bird} as documented in {@code COMMANDS.md}: who may send a bird, what stops one being sent, how the
 * flight time follows from {@code birdSpeed}, and what is delivered when the bird lands.
 *
 * <p>{@link MedievalRoleplayEngine} is a {@code JavaPlugin}, whose constructor refuses to run outside a server's
 * plugin class loader, so the plugin is allocated without running any constructor and only the fields
 * {@link BirdCommand} reads are filled in. The bird's landing is scheduled on {@link BukkitTestServer}'s scheduler,
 * which only records it, so a test can check the delay and then run the task itself.</p>
 */
public class BirdCommandTest {
    private static final ChatColor POSITIVE = ChatColor.GREEN;
    private static final ChatColor NEGATIVE = ChatColor.RED;
    private static final ChatColor NEUTRAL = ChatColor.AQUA;
    private static final UUID SENDER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID TARGET_UUID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final String[] GREETING = {"Mira", "Meet", "me", "at", "dawn"};
    private static final String FLIES_OFF = POSITIVE + "The bird flies off with your message.";
    private static final String BAD_SPEED = NEGATIVE
            + "Birds can't be sent right now, because the server's birdSpeed setting must be at least 1.";

    private final EphemeralData ephemeralData = new EphemeralData();
    private final StubConfigService configService = new StubConfigService();
    private final RecordingMessenger messenger = new RecordingMessenger();
    private final Set<String> senderPermissions = new HashSet<>(Collections.singletonList("rp.bird"));
    private final List<String> senderReceived = new ArrayList<>();
    private final List<String> targetReceived = new ArrayList<>();
    private final World world = world("world");

    private BirdCommand birdCommand;
    private Player sender;
    private Player target;

    @Before
    public void setUp() throws Exception {
        birdCommand = new BirdCommand(plugin());
        sender = player("Aldric", SENDER_UUID, senderPermissions, senderReceived, new Location(world, 0, 64, 0));
        target = player("Mira", TARGET_UUID, Collections.<String>emptySet(), targetReceived,
                new Location(world, 130, 64, 0));
        BukkitTestServer.setOnlinePlayers(sender, target);
        BukkitTestServer.clearScheduledTasks();
    }

    @After
    public void tearDown() {
        BukkitTestServer.setOnlinePlayers();
        BukkitTestServer.clearScheduledTasks();
    }

    @Test
    public void aSenderThatIsNotAPlayerIsIgnored() {
        birdCommand.sendBird(console(), GREETING);

        assertTrue(BukkitTestServer.scheduledTasks().isEmpty());
    }

    @Test
    public void birdAndDefaultEachGrantTheCommand() {
        for (String node : Arrays.asList("rp.bird", "rp.default")) {
            senderPermissions.clear();
            senderPermissions.add(node);
            ephemeralData.getPlayersWithBusyBirds().clear();
            senderReceived.clear();

            birdCommand.sendBird(sender, GREETING);

            assertEquals(node, Collections.singletonList(FLIES_OFF), senderReceived);
        }
    }

    @Test
    public void withoutPermissionThePlayerIsToldAndNoBirdIsSent() {
        senderPermissions.clear();

        birdCommand.sendBird(sender, GREETING);

        assertEquals(Collections.singletonList(
                NEGATIVE + "Sorry! In order to use this command, you need the following permission: 'rp.bird'"),
                senderReceived);
        assertNoBirdSent();
    }

    @Test
    public void onlyOneBirdPerSenderMayBeInFlight() {
        birdCommand.sendBird(sender, GREETING);
        senderReceived.clear();

        birdCommand.sendBird(sender, GREETING);

        assertEquals(Collections.singletonList(NEGATIVE + "Your bird is already on a mission!"), senderReceived);
        assertEquals(1, BukkitTestServer.scheduledTasks().size());
    }

    @Test
    public void fewerThanTwoArgumentsShowsTheUsage() {
        birdCommand.sendBird(sender, new String[]{"Mira"});

        assertEquals(Collections.singletonList(NEGATIVE + "Usage: /bird (player-name) (message)"), senderReceived);
        assertNoBirdSent();
    }

    @Test
    public void aTargetWhoIsNotOnlineIsReported() {
        birdCommand.sendBird(sender, new String[]{"Nobody", "Hello"});

        assertEquals(Collections.singletonList(NEGATIVE + "That player isn't online!"), senderReceived);
        assertNoBirdSent();
    }

    @Test
    public void aTargetInAnotherWorldIsRefused() {
        Player elsewhere = player("Ivo", TARGET_UUID, Collections.<String>emptySet(), targetReceived,
                new Location(world("world_nether"), 0, 64, 0));
        BukkitTestServer.setOnlinePlayers(sender, elsewhere);

        birdCommand.sendBird(sender, new String[]{"Ivo", "Hello"});

        assertEquals(Collections.singletonList(NEGATIVE + "You can't send a bird to a player in another world."),
                senderReceived);
        assertNoBirdSent();
    }

    @Test
    public void theFlightTakesDistanceOverBirdSpeedInWholeSeconds() {
        configService.birdSpeed = 20;

        birdCommand.sendBird(sender, GREETING);

        assertEquals(Collections.singletonList(FLIES_OFF), senderReceived);
        assertEquals(Collections.singletonList(SENDER_UUID), ephemeralData.getPlayersWithBusyBirds());
        assertEquals(1, BukkitTestServer.scheduledTasks().size());
        assertEquals(6 * 20L, BukkitTestServer.scheduledTasks().get(0).delayTicks);
    }

    @Test
    public void aBirdSpeedOfOneIsTheSlowestAccepted() {
        configService.birdSpeed = 1;

        birdCommand.sendBird(sender, GREETING);

        assertEquals(Collections.singletonList(FLIES_OFF), senderReceived);
        assertEquals(130 * 20L, BukkitTestServer.scheduledTasks().get(0).delayTicks);
    }

    @Test
    public void aBirdSpeedBelowOneIsReportedAndNoBirdIsSent() {
        for (int birdSpeed : new int[]{0, -5}) {
            configService.birdSpeed = birdSpeed;
            senderReceived.clear();

            birdCommand.sendBird(sender, GREETING);

            assertEquals("birdSpeed " + birdSpeed, Collections.singletonList(BAD_SPEED), senderReceived);
            assertNoBirdSent();
        }
    }

    @Test
    public void whenTheBirdLandsTheMessageIsDeliveredAndTheSenderIsFreeAgain() {
        birdCommand.sendBird(sender, GREETING);
        senderReceived.clear();

        BukkitTestServer.scheduledTasks().get(0).task.run();

        assertEquals(Arrays.asList(
                POSITIVE + "A bird lands nearby and drops a message at your feet! It was sent by Aldric. It reads:\n",
                POSITIVE + "" + ChatColor.ITALIC + "'Meet me at dawn'"), targetReceived);
        assertEquals(Collections.singletonList(POSITIVE + "Your bird has reached Mira!"), senderReceived);
        assertTrue(ephemeralData.getPlayersWithBusyBirds().isEmpty());
        assertEquals(Collections.singletonList(
                NEUTRAL + "A bird lands nearby and drops a message at the feet of Mira!"), messenger.landingNotices);
    }

    private void assertNoBirdSent() {
        assertTrue(BukkitTestServer.scheduledTasks().isEmpty());
        assertTrue(ephemeralData.getPlayersWithBusyBirds().isEmpty());
    }

    private static Player player(final String name, final UUID uuid, final Set<String> permissions,
                                 final List<String> received, final Location location) {
        return (Player) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class<?>[]{Player.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        switch (method.getName()) {
                            case "hasPermission":
                                return permissions.contains((String) args[0]);
                            case "getName":
                            case "toString":
                                return name;
                            case "getUniqueId":
                                return uuid;
                            case "getLocation":
                                return location.clone();
                            case "sendMessage":
                                received.add((String) args[0]);
                                return null;
                            case "hashCode":
                                return System.identityHashCode(proxy);
                            case "equals":
                                return proxy == args[0];
                            default:
                                throw new AssertionError("Unexpected call to Player." + method.getName());
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
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Object unsafe = theUnsafe.get(null);
        Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
        MedievalRoleplayEngine plugin =
                (MedievalRoleplayEngine) allocateInstance.invoke(unsafe, MedievalRoleplayEngine.class);
        setField(plugin, "ephemeralData", ephemeralData);
        setField(plugin, "configService", configService);
        setField(plugin, "colorChecker", new FixedColorChecker());
        setField(plugin, "messenger", messenger);
        setField(plugin, "argumentParser", new ArgumentParser());
        return plugin;
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = MedievalRoleplayEngine.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    /** Answers {@code birdSpeed} from a field, so no {@code config.yml} is needed. */
    private static final class StubConfigService extends ConfigService {
        private int birdSpeed = 20;

        StubConfigService() {
            super(null);
        }

        @Override
        public int getInt(String option) {
            if (!option.equals("birdSpeed")) {
                throw new AssertionError("Unexpected option: " + option);
            }
            return birdSpeed;
        }
    }

    /** Fixed alert colors, so messages can be compared without a {@code config.yml}. */
    private static final class FixedColorChecker extends ColorChecker {
        FixedColorChecker() {
            super(null);
        }

        @Override
        public ChatColor getPositiveAlertColor() {
            return POSITIVE;
        }

        @Override
        public ChatColor getNegativeAlertColor() {
            return NEGATIVE;
        }

        @Override
        public ChatColor getNeutralAlertColor() {
            return NEUTRAL;
        }
    }

    /** Records the notice to bystanders instead of looking for nearby players. */
    private static final class RecordingMessenger extends Messenger {
        private final List<String> landingNotices = new ArrayList<>();

        RecordingMessenger() {
            super(null);
        }

        @Override
        public int sendRPMessageToPlayersWithinDistanceExcludingTarget(Player sender, String message, int distance) {
            assertEquals(10, distance);
            landingNotices.add(message);
            return 0;
        }
    }
}
