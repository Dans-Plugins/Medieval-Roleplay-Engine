package dansplugins.rpsystem.commands.roll;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.utils.ColorChecker;
import dansplugins.rpsystem.utils.Messenger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Characterizes {@code /roll} and {@code /dice} as documented in {@code COMMANDS.md}: the permission check, how dice
 * notation is parsed and bounded, the message players see and the 25-block broadcast.
 *
 * <p>{@link MedievalRoleplayEngine} is a {@code JavaPlugin}, whose constructor refuses to run outside a server's
 * plugin class loader, so the plugin is allocated without running any constructor and only the two fields
 * {@link RollCommand} reads, {@code colorChecker} and {@code messenger}, are filled in with recording stand-ins. The
 * command's {@link Random} is replaced with a scripted one so that every roll is known.</p>
 */
public class RollCommandTest {
    private static final ChatColor NEUTRAL = ChatColor.AQUA;
    private static final ChatColor NEGATIVE = ChatColor.RED;
    private static final String PLAYER_NAME = "Aldric";
    private static final String ROLL_PREFIX = NEUTRAL + "" + ChatColor.ITALIC + PLAYER_NAME + " rolled ";

    private final RecordingMessenger messenger = new RecordingMessenger();
    private final ScriptedRandom random = new ScriptedRandom();
    private final List<String> received = new ArrayList<>();
    private final Set<String> permissions = new HashSet<>(Collections.singletonList("rp.roll"));

    private RollCommand rollCommand;
    private Player player;

    @Before
    public void setUp() throws Exception {
        rollCommand = new RollCommand(pluginWith(new FixedColorChecker(), messenger));
        setField(RollCommand.class, rollCommand, "random", random);
        player = player();
    }

    @Test
    public void aSenderThatIsNotAPlayerIsRejectedWithoutAMessage() {
        CommandSender console = (CommandSender) Proxy.newProxyInstance(CommandSender.class.getClassLoader(),
                new Class<?>[]{CommandSender.class}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        throw new AssertionError("The console sender should not be consulted: " + method.getName());
                    }
                });

        assertFalse(rollCommand.rollDice(console, new String[]{"2d6"}));
        assertTrue(messenger.broadcasts.isEmpty());
    }

    @Test
    public void aPlayerWithoutAnyAcceptedNodeIsToldWhichNodesAreAccepted() {
        permissions.clear();

        assertTrue(rollCommand.rollDice(player, new String[]{"2d6"}));

        assertEquals(Collections.singletonList(NEGATIVE + "Sorry! In order to use this command, you need one of the"
                + " following permissions: 'rp.roll', 'rp.dice', 'rp.default'"), received);
        assertTrue(random.bounds.isEmpty());
        assertTrue(messenger.broadcasts.isEmpty());
    }

    @Test
    public void eachOfTheThreeAcceptedNodesIsEnoughOnItsOwn() {
        for (String node : Arrays.asList("rp.roll", "rp.dice", "rp.default")) {
            permissions.clear();
            permissions.add(node);
            received.clear();
            random.script(4);

            assertTrue(rollCommand.rollDice(player, new String[0]));
            assertEquals(node, Collections.singletonList(ROLL_PREFIX + "1d20: 4 = 4"), received);
        }
    }

    @Test
    public void noArgumentRollsASingleD20() {
        random.script(17);

        assertTrue(rollCommand.rollDice(player, new String[0]));

        assertEquals(Collections.singletonList(20), random.bounds);
        assertEquals(Collections.singletonList(ROLL_PREFIX + "1d20: 17 = 17"), received);
    }

    @Test
    public void theRollerSeesTheResultAndItIsBroadcastWithinTwentyFiveBlocksExcludingThem() {
        random.script(9);

        rollCommand.rollDice(player, new String[]{"d20"});

        String expected = ROLL_PREFIX + "1d20: 9 = 9";
        assertEquals(Collections.singletonList(expected), received);
        assertEquals(Collections.singletonList(new Broadcast(player, expected, 25)), messenger.broadcasts);
    }

    @Test
    public void severalDiceAreListedAndSummedWithAPositiveModifier() {
        random.script(6, 1);

        rollCommand.rollDice(player, new String[]{"2d6+3"});

        assertEquals(Arrays.asList(6, 6), random.bounds);
        assertEquals(Collections.singletonList(ROLL_PREFIX + "2d6+3: [6, 1] +3 = 10"), received);
    }

    @Test
    public void aNegativeModifierIsSubtracted() {
        random.script(8, 2, 5);

        rollCommand.rollDice(player, new String[]{"3d8-2"});

        assertEquals(Collections.singletonList(ROLL_PREFIX + "3d8-2: [8, 2, 5] -2 = 13"), received);
    }

    @Test
    public void aTotalMayGoBelowOneWhenTheModifierOutweighsTheRoll() {
        random.script(1);

        rollCommand.rollDice(player, new String[]{"d4-10"});

        assertEquals(Collections.singletonList(ROLL_PREFIX + "1d4-10: 1 -10 = -9"), received);
    }

    @Test
    public void aBareNumberIsTheDieSizeOfASingleDie() {
        random.script(3);

        rollCommand.rollDice(player, new String[]{"20"});

        assertEquals(Collections.singletonList(20), random.bounds);
        assertEquals(Collections.singletonList(ROLL_PREFIX + "1d20: 3 = 3"), received);
    }

    @Test
    public void aBareNumberMayCarryAModifierToo() {
        random.script(3);

        rollCommand.rollDice(player, new String[]{"12+1"});

        assertEquals(Collections.singletonList(ROLL_PREFIX + "1d12+1: 3 +1 = 4"), received);
    }

    @Test
    public void notationIsCaseInsensitiveAndShownInLowerCase() {
        random.script(2, 5);

        rollCommand.rollDice(player, new String[]{"2D6"});

        assertEquals(Collections.singletonList(ROLL_PREFIX + "2d6: [2, 5] = 7"), received);
    }

    @Test
    public void leadingZerosAndAZeroModifierAreNormalizedAway() {
        random.script(4, 4);

        rollCommand.rollDice(player, new String[]{"02d06+0"});

        assertEquals(Collections.singletonList(ROLL_PREFIX + "2d6: [4, 4] = 8"), received);
    }

    @Test
    public void onlyTheFirstArgumentIsRead() {
        random.script(5);

        rollCommand.rollDice(player, new String[]{"d6", "for", "initiative"});

        assertEquals(Collections.singletonList(ROLL_PREFIX + "1d6: 5 = 5"), received);
    }

    @Test
    public void theLargestAcceptedRollIsAHundredD10000PlusTenThousand() {
        Integer[] rolls = new Integer[100];
        Arrays.fill(rolls, 10000);
        random.script(rolls);

        rollCommand.rollDice(player, new String[]{"100d10000+10000"});

        assertEquals(100, random.bounds.size());
        assertEquals(1, received.size());
        assertTrue(received.get(0), received.get(0).startsWith(ROLL_PREFIX + "100d10000+10000: [10000, 10000, "));
        assertTrue(received.get(0), received.get(0).endsWith(", 10000] +10000 = 1010000"));
    }

    @Test
    public void notationThatDoesNotParseIsRejectedWithExamples() {
        for (String input : Arrays.asList("abc", "2d", "d", "2x6", "1d6+", "+3", "-1", "1d6*2", "2d6+1+1", " 2d6",
                "")) {
            assertRejected(input, NEGATIVE + "'" + input + "' is not valid dice notation."
                    + " Use e.g. '20', 'd20', '2d6', or '3d8+3'.");
        }
    }

    @Test
    public void zeroDiceOrAZeroSidedDieIsRejected() {
        for (String input : Arrays.asList("0d6", "1d0", "d0", "0")) {
            assertRejected(input, NEGATIVE + "The number of dice and die size must each be at least 1.");
        }
    }

    @Test
    public void tooManyDiceOrTooLargeADieIsRejected() {
        for (String input : Arrays.asList("101d6", "1d10001", "10001")) {
            assertRejected(input, NEGATIVE + "Maximum allowed: 100 dice with up to a d10000.");
        }
    }

    @Test
    public void aModifierBeyondTenThousandEitherWayIsRejected() {
        for (String input : Arrays.asList("1d6+10001", "1d6-10001")) {
            assertRejected(input, NEGATIVE + "Modifier must be between -10000 and +10000.");
        }
    }

    @Test
    public void aValueTooLargeForAnIntIsRejectedWithTheLimits() {
        for (String input : Arrays.asList("99999999999d6", "1d99999999999", "1d6+99999999999")) {
            assertRejected(input, NEGATIVE + "'" + input + "' contains a value that is too large."
                    + " Maximum: 100 dice, d10000, modifier ±10000.");
        }
    }

    private void assertRejected(String input, String expectedMessage) {
        received.clear();

        assertTrue(input, rollCommand.rollDice(player, new String[]{input}));

        assertEquals(input, Collections.singletonList(expectedMessage), received);
        assertTrue(input, random.bounds.isEmpty());
        assertTrue(input, messenger.broadcasts.isEmpty());
    }

    private Player player() {
        return (Player) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class<?>[]{Player.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        switch (method.getName()) {
                            case "hasPermission":
                                return permissions.contains((String) args[0]);
                            case "getName":
                                return PLAYER_NAME;
                            case "sendMessage":
                                received.add((String) args[0]);
                                return null;
                            case "hashCode":
                                return System.identityHashCode(proxy);
                            case "equals":
                                return proxy == args[0];
                            case "toString":
                                return PLAYER_NAME;
                            default:
                                throw new AssertionError("Unexpected call to Player." + method.getName());
                        }
                    }
                });
    }

    /** Allocates the plugin without running {@code JavaPlugin}'s constructor, then sets the fields under test. */
    private static MedievalRoleplayEngine pluginWith(ColorChecker colorChecker, Messenger messenger) throws Exception {
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Object unsafe = theUnsafe.get(null);
        Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
        MedievalRoleplayEngine plugin =
                (MedievalRoleplayEngine) allocateInstance.invoke(unsafe, MedievalRoleplayEngine.class);
        setField(MedievalRoleplayEngine.class, plugin, "colorChecker", colorChecker);
        setField(MedievalRoleplayEngine.class, plugin, "messenger", messenger);
        return plugin;
    }

    private static void setField(Class<?> owner, Object target, String name, Object value) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    /** Fixed alert colors, so messages can be compared without a {@code config.yml}. */
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

    /** Records the broadcast instead of looking for nearby players. */
    private static final class RecordingMessenger extends Messenger {
        private final List<Broadcast> broadcasts = new ArrayList<>();

        RecordingMessenger() {
            super(null);
        }

        @Override
        public int sendRPMessageToPlayersWithinDistanceExcludingTarget(Player sender, String message, int distance) {
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

    /**
     * Returns scripted die faces. {@link RollCommand} adds one to {@code nextInt(dieSize)}, so a face of {@code n} is
     * returned as {@code n - 1}; each requested bound is recorded.
     */
    private static final class ScriptedRandom extends Random {
        private final List<Integer> faces = new ArrayList<>();
        private final List<Integer> bounds = new ArrayList<>();

        void script(Integer... scripted) {
            faces.clear();
            bounds.clear();
            faces.addAll(Arrays.asList(scripted));
        }

        @Override
        public int nextInt(int bound) {
            bounds.add(bound);
            if (faces.isEmpty()) {
                throw new AssertionError("No die face scripted for a d" + bound);
            }
            int face = faces.remove(0);
            if (face < 1 || face > bound) {
                throw new AssertionError("Scripted face " + face + " cannot come up on a d" + bound);
            }
            return face - 1;
        }
    }
}
