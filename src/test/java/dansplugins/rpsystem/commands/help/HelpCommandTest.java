package dansplugins.rpsystem.commands.help;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.config.ConfigService;
import dansplugins.rpsystem.utils.ColorChecker;
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
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Covers {@code /rphelp} as documented in {@code COMMANDS.md}: who may see the list, which lines it holds, and that
 * the chat commands are left out while {@code chatFeaturesEnabled} is {@code false}.
 *
 * <p>{@link MedievalRoleplayEngine} is a {@code JavaPlugin}, whose constructor refuses to run outside a server's
 * plugin class loader, so the plugin is allocated without running any constructor and only the fields
 * {@link HelpCommand} reads are filled in, the version included.</p>
 */
public class HelpCommandTest {
    private static final ChatColor NEUTRAL = ChatColor.AQUA;
    private static final ChatColor NEGATIVE = ChatColor.RED;
    private static final String VERSION = "v9.8.7-test";

    private static final List<String> ALWAYS_SHOWN_BEFORE_CHAT = Arrays.asList(
            NEUTRAL + " == Medieval Roleplay Engine " + VERSION + " Commands == ",
            NEUTRAL + "/rphelp - Show a list of useful commands for the plugin.",
            NEUTRAL + "/card help - Show a list of commands useful for managing character cards.",
            NEUTRAL + "/bird - Send a bird to another player.",
            NEUTRAL + "/roll or /dice - Roll dice using standard notation (e.g. 2d6, 1d20+5, 3d8-1).",
            NEUTRAL + "/title - Rename an unwritten book.");
    private static final List<String> CHAT_COMMANDS = Arrays.asList(
            NEUTRAL + "/rp - Enter local (RP) chat.",
            NEUTRAL + "/ooc - Enter global (OOC) chat.",
            NEUTRAL + "/emote or /me - Send an emote to nearby players.",
            NEUTRAL + "/yell (message)- Send a single messages to far away players.",
            NEUTRAL + "/whisper (message) - Send a single message to very close players.",
            NEUTRAL + "/lo (message) - Send an single OOC message to nearby players.");
    private static final String CONFIG_LINE = NEUTRAL + "/rpconfig - View and change config options.";

    private final Set<String> permissions = new HashSet<>(Collections.singletonList("rp.help"));
    private final List<String> received = new ArrayList<>();
    private final StubConfigService configService = new StubConfigService();

    private HelpCommand helpCommand;
    private Player player;

    @Before
    public void setUp() throws Exception {
        helpCommand = new HelpCommand(plugin());
        player = player();
    }

    @Test
    public void aSenderThatIsNotAPlayerIsIgnored() {
        helpCommand.showListOfCommands(console());

        assertEquals(Collections.<String>emptyList(), configService.booleansRead);
    }

    @Test
    public void helpRphelpAndDefaultEachGrantTheCommand() {
        for (String node : Arrays.asList("rp.help", "rp.rphelp", "rp.default")) {
            permissions.clear();
            permissions.add(node);
            received.clear();

            helpCommand.showListOfCommands(player);

            assertEquals(node, fullList(), received);
        }
    }

    @Test
    public void withoutPermissionThePlayerIsToldWhichNodeToGetAndNoListIsShown() {
        permissions.clear();

        helpCommand.showListOfCommands(player);

        assertEquals(Collections.singletonList(
                NEGATIVE + "Sorry! In order to use this command, you need the following permission: 'rp.help'"),
                received);
        assertEquals(Collections.<String>emptyList(), configService.booleansRead);
    }

    @Test
    public void withChatFeaturesEnabledEveryCommandIsListedInOrder() {
        helpCommand.showListOfCommands(player);

        assertEquals(fullList(), received);
        assertEquals(Collections.singletonList("chatFeaturesEnabled"), configService.booleansRead);
    }

    @Test
    public void withChatFeaturesDisabledTheChatCommandsAreLeftOut() {
        configService.chatFeaturesEnabled = false;

        helpCommand.showListOfCommands(player);

        List<String> expected = new ArrayList<>(ALWAYS_SHOWN_BEFORE_CHAT);
        expected.add(CONFIG_LINE);
        assertEquals(expected, received);
    }

    @Test
    public void rpconfigIsListedEvenToAPlayerWhoCannotUseIt() {
        permissions.clear();
        permissions.add("rp.help");

        helpCommand.showListOfCommands(player);

        assertEquals(CONFIG_LINE, received.get(received.size() - 1));
    }

    private static List<String> fullList() {
        List<String> expected = new ArrayList<>(ALWAYS_SHOWN_BEFORE_CHAT);
        expected.addAll(CHAT_COMMANDS);
        expected.add(CONFIG_LINE);
        return expected;
    }

    private Player player() {
        return (Player) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class<?>[]{Player.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        switch (method.getName()) {
                            case "hasPermission":
                                return permissions.contains((String) args[0]);
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
        setField(plugin, "colorChecker", new FixedColorChecker());
        setField(plugin, "configService", configService);
        setField(plugin, "pluginVersion", VERSION);
        return plugin;
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = MedievalRoleplayEngine.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    /** Answers {@code chatFeaturesEnabled} from a field and records each option read, so no config is needed. */
    private static final class StubConfigService extends ConfigService {
        private boolean chatFeaturesEnabled = true;
        private final List<String> booleansRead = new ArrayList<>();

        StubConfigService() {
            super(null);
        }

        @Override
        public boolean getBoolean(String option) {
            booleansRead.add(option);
            if (!option.equals("chatFeaturesEnabled")) {
                throw new AssertionError("Unexpected option: " + option);
            }
            return chatFeaturesEnabled;
        }
    }

    /** Fixed alert colors, so messages can be compared without a {@code config.yml}. */
    private static final class FixedColorChecker extends ColorChecker {
        FixedColorChecker() {
            super(null);
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
}
