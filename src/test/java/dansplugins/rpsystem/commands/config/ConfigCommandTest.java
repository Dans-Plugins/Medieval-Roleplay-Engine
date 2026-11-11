package dansplugins.rpsystem.commands.config;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Covers how {@code /rpconfig} is routed, as documented in {@code COMMANDS.md}: who may use it, which sub-commands
 * it recognizes, and what is handed on to {@link ConfigService}. What {@code setConfigOption} then does with an
 * option is covered by {@code ConfigServiceTest}; here {@link ConfigService} is a stub that records what it is asked.
 *
 * <p>{@link MedievalRoleplayEngine} is a {@code JavaPlugin}, whose constructor refuses to run outside a server's
 * plugin class loader, so the plugin is allocated without running any constructor and only the fields
 * {@link ConfigCommand} reads are filled in.</p>
 */
public class ConfigCommandTest {
    private static final ChatColor NEGATIVE = ChatColor.RED;
    private static final String NO_PERMISSION =
            NEGATIVE + "You don't have permission to configure Medieval Roleplay Engine!";
    private static final String VALID_SUBCOMMANDS = NEGATIVE + "Valid subcommands: show, set";
    private static final String SET_USAGE = NEGATIVE + "Usage: /rpconfig set (option) (value)";

    private final Set<String> permissions = new HashSet<>(Collections.singletonList("rp.config"));
    private final List<String> received = new ArrayList<>();
    private final RecordingConfigService configService = new RecordingConfigService();

    private ConfigCommand configCommand;
    private Player player;

    @Before
    public void setUp() throws Exception {
        configCommand = new ConfigCommand(plugin());
        player = player();
    }

    @Test
    public void aSenderThatIsNotAPlayerIsToldOnlyPlayersCanUseIt() {
        List<String> consoleReceived = new ArrayList<>();

        boolean handled = configCommand.handleConfigAccess(console(consoleReceived), new String[]{"show"});

        assertFalse(handled);
        assertEquals(Collections.singletonList("Only players can use this command."), consoleReceived);
        assertNothingAskedOfTheConfig();
    }

    @Test
    public void configAndAdminEachGrantTheCommand() {
        for (String node : Arrays.asList("rp.config", "rp.admin")) {
            permissions.clear();
            permissions.add(node);
            configService.listsSent.clear();

            boolean handled = configCommand.handleConfigAccess(player, new String[]{"show"});

            assertTrue(node, handled);
            assertEquals(node, Collections.singletonList(player), configService.listsSent);
        }
    }

    @Test
    public void defaultDoesNotGrantTheCommand() {
        permissions.clear();
        permissions.add("rp.default");

        boolean handled = configCommand.handleConfigAccess(player, new String[]{"show"});

        assertFalse(handled);
        assertEquals(Collections.singletonList(NO_PERMISSION), received);
        assertNothingAskedOfTheConfig();
    }

    @Test
    public void withoutPermissionThePlayerIsToldAndNothingIsShownOrSet() {
        permissions.clear();

        for (String[] args : Arrays.asList(new String[]{"show"}, new String[]{"set", "localChatRadius", "30"})) {
            received.clear();

            boolean handled = configCommand.handleConfigAccess(player, args);

            assertFalse(Arrays.toString(args), handled);
            assertEquals(Arrays.toString(args), Collections.singletonList(NO_PERMISSION), received);
        }
        assertNothingAskedOfTheConfig();
    }

    @Test
    public void thePermissionIsCheckedBeforeTheSubCommand() {
        permissions.clear();

        configCommand.handleConfigAccess(player, new String[0]);

        assertEquals(Collections.singletonList(NO_PERMISSION), received);
    }

    @Test
    public void noSubCommandListsTheValidOnes() {
        boolean handled = configCommand.handleConfigAccess(player, new String[0]);

        assertFalse(handled);
        assertEquals(Collections.singletonList(VALID_SUBCOMMANDS), received);
        assertNothingAskedOfTheConfig();
    }

    @Test
    public void anUnknownSubCommandListsTheValidOnes() {
        boolean handled = configCommand.handleConfigAccess(player, new String[]{"reload"});

        assertFalse(handled);
        assertEquals(Collections.singletonList(VALID_SUBCOMMANDS), received);
        assertNothingAskedOfTheConfig();
    }

    @Test
    public void showSendsTheConfigListToThePlayerAndNothingElse() {
        boolean handled = configCommand.handleConfigAccess(player, new String[]{"show"});

        assertTrue(handled);
        assertEquals(Collections.singletonList(player), configService.listsSent);
        assertEquals(Collections.<String>emptyList(), configService.optionsSet);
        assertEquals(Collections.<String>emptyList(), received);
    }

    @Test
    public void showIgnoresAnyWordsAfterIt() {
        configCommand.handleConfigAccess(player, new String[]{"show", "localChatRadius"});

        assertEquals(Collections.singletonList(player), configService.listsSent);
    }

    @Test
    public void subCommandsMatchRegardlessOfCase() {
        configCommand.handleConfigAccess(player, new String[]{"SHOW"});
        configCommand.handleConfigAccess(player, new String[]{"Set", "localChatRadius", "30"});

        assertEquals(Collections.singletonList(player), configService.listsSent);
        assertEquals(Collections.singletonList("localChatRadius=30"), configService.optionsSet);
    }

    @Test
    public void setHandsTheOptionAndValueToTheConfigServiceAsTyped() {
        boolean handled = configCommand.handleConfigAccess(player, new String[]{"set", "localChatRadius", "30"});

        assertTrue(handled);
        assertEquals(Collections.singletonList("localChatRadius=30"), configService.optionsSet);
        assertEquals(Collections.singletonList(player), configService.settersTold);
        assertEquals(Collections.<Player>emptyList(), configService.listsSent);
        assertEquals(Collections.<String>emptyList(), received);
    }

    @Test
    public void setPassesOnlyTheFirstWordOfTheValue() {
        configCommand.handleConfigAccess(player, new String[]{"set", "localChatColor", "dark", "green"});

        assertEquals(Collections.singletonList("localChatColor=dark"), configService.optionsSet);
    }

    @Test
    public void setWithoutAValueOrOptionShowsTheUsage() {
        for (String[] args : Arrays.asList(new String[]{"set"}, new String[]{"set", "localChatRadius"})) {
            received.clear();

            boolean handled = configCommand.handleConfigAccess(player, args);

            assertFalse(Arrays.toString(args), handled);
            assertEquals(Arrays.toString(args), Collections.singletonList(SET_USAGE), received);
        }
        assertNothingAskedOfTheConfig();
    }

    private void assertNothingAskedOfTheConfig() {
        assertEquals(Collections.<Player>emptyList(), configService.listsSent);
        assertEquals(Collections.<String>emptyList(), configService.optionsSet);
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

    private static CommandSender console(final List<String> consoleReceived) {
        return (CommandSender) Proxy.newProxyInstance(CommandSender.class.getClassLoader(),
                new Class<?>[]{CommandSender.class}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        if (method.getName().equals("sendMessage") && args.length == 1 && args[0] instanceof String) {
                            consoleReceived.add((String) args[0]);
                            return null;
                        }
                        throw new AssertionError("Unexpected call to CommandSender." + method.getName());
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
        setField(plugin, "configService", configService);
        setField(plugin, "colorChecker", new FixedColorChecker());
        return plugin;
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = MedievalRoleplayEngine.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    /** Records what the command asks of the config instead of reading or writing a {@code config.yml}. */
    private static final class RecordingConfigService extends ConfigService {
        private final List<Player> listsSent = new ArrayList<>();
        private final List<String> optionsSet = new ArrayList<>();
        private final List<Player> settersTold = new ArrayList<>();

        RecordingConfigService() {
            super(null);
        }

        @Override
        public void sendPlayerConfigList(Player player) {
            listsSent.add(player);
        }

        @Override
        public void setConfigOption(String option, String value, Player player) {
            optionsSet.add(option + "=" + value);
            settersTold.add(player);
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
    }
}
