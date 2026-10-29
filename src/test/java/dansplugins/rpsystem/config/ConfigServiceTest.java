package dansplugins.rpsystem.config;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Characterizes {@link ConfigService}: how {@code /rpconfig set} parses and stores a value, how the defaults are
 * written on a fresh install and backfilled on an upgrade, and how the usage-reporting options resolve.
 *
 * <p>{@link MedievalRoleplayEngine} is a {@code JavaPlugin}, whose constructor refuses to run outside a server's plugin
 * class loader, so the plugin is allocated without running any constructor and only what {@link ConfigService} reads is
 * filled in: the running version, the {@link ColorChecker}, and {@code JavaPlugin}'s in-memory config and the file
 * {@code saveConfig()} writes it to.</p>
 */
public class ConfigServiceTest {
    private static final String VERSION = "v9.9.9-test";
    private static final String POSITIVE = ChatColor.GREEN.toString();

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private final List<String> received = new ArrayList<>();
    private final Player player = player(received);

    private FileConfiguration config;
    private File configFile;
    private ConfigService configService;

    @Before
    public void setUp() throws Exception {
        config = new YamlConfiguration();
        configFile = new File(temporaryFolder.getRoot(), "config.yml");
        MedievalRoleplayEngine plugin = pluginWith(config, configFile);
        configService = new ConfigService(plugin);
    }

    // /rpconfig set

    @Test
    public void anOptionThatIsNotSetIsReportedAsNotFoundAndNothingIsSaved() {
        configService.setConfigOption("noSuchOption", "5", player);

        assertEquals(Collections.singletonList(ChatColor.RED + "The option 'noSuchOption' wasn't found."), received);
        assertFalse(config.isSet("noSuchOption"));
        assertFalse(configFile.exists());
        assertFalse(configService.hasBeenAltered());
    }

    @Test
    public void optionNamesAreMatchedCaseSensitivelyAgainstTheConfig() {
        // The integer/boolean branches compare case-insensitively, but isSet() is consulted first and is not.
        config.set("localChatRadius", 25);

        configService.setConfigOption("localchatradius", "40", player);

        assertEquals(Collections.singletonList(ChatColor.RED + "The option 'localchatradius' wasn't found."), received);
        assertEquals(25, config.getInt("localChatRadius"));
    }

    @Test
    public void theVersionCannotBeSet() {
        config.set("version", VERSION);

        configService.setConfigOption("version", "v0", player);

        assertEquals(Collections.singletonList(ChatColor.RED + "Cannot set version!"), received);
        assertEquals(VERSION, config.getString("version"));
        assertFalse(configFile.exists());
        assertFalse(configService.hasBeenAltered());
    }

    @Test
    public void anIntegerOptionIsStoredAsAnIntegerAndSaved() {
        configService.saveConfigDefaults();

        configService.setConfigOption("yellChatRadius", "75", player);

        assertEquals(Collections.singletonList(POSITIVE + "Integer set!"), received);
        assertEquals(Integer.valueOf(75), config.get("yellChatRadius"));
        assertEquals(75, YamlConfiguration.loadConfiguration(configFile).getInt("yellChatRadius"));
        assertTrue(configService.hasBeenAltered());
    }

    @Test
    public void everyRadiusCooldownAndSpeedOptionIsParsedAsAnInteger() {
        configService.saveConfigDefaults();
        String[] options = {"localChatRadius", "whisperChatRadius", "yellChatRadius", "changeNameCooldown",
                "emoteRadius", "localOOCChatRadius", "birdSpeed"};

        for (String option : options) {
            configService.setConfigOption(option, "7", player);
            assertEquals(option, Integer.valueOf(7), config.get(option));
        }
        assertEquals(Collections.nCopies(options.length, POSITIVE + "Integer set!"), received);
    }

    @Test
    public void aNonNumericValueForAnIntegerOptionIsExplainedAndLeavesTheOptionUnchanged() {
        configService.saveConfigDefaults();
        long savedAt = configFile.lastModified();
        String savedContents = YamlConfiguration.loadConfiguration(configFile).saveToString();

        configService.setConfigOption("localChatRadius", "far", player);

        assertEquals(Collections.singletonList(ChatColor.RED + "localChatRadius must be a whole number."), received);
        assertEquals(25, config.getInt("localChatRadius"));
        assertEquals(savedAt, configFile.lastModified());
        assertEquals(savedContents, YamlConfiguration.loadConfiguration(configFile).saveToString());
        assertFalse(configService.hasBeenAltered());
    }

    @Test
    public void aDecimalValueForAnIntegerOptionIsAlsoRejected() {
        configService.saveConfigDefaults();

        configService.setConfigOption("birdSpeed", "2.5", player);

        assertEquals(Collections.singletonList(ChatColor.RED + "birdSpeed must be a whole number."), received);
        assertEquals(20, config.getInt("birdSpeed"));
    }

    @Test
    public void everyOnOffOptionIsParsedAsABoolean() {
        configService.saveConfigDefaults();
        String[] options = {"rightClickToViewCard", "chatFeaturesEnabled", "debugMode", "logChat"};

        for (String option : options) {
            configService.setConfigOption(option, "TRUE", player);
            assertEquals(option, Boolean.TRUE, config.get(option));
        }
        assertEquals(Collections.nCopies(options.length, POSITIVE + "Boolean set!"), received);
    }

    @Test
    public void anythingOtherThanTrueIsStoredAsFalse() {
        configService.saveConfigDefaults();

        configService.setConfigOption("rightClickToViewCard", "yes", player);

        assertEquals(Collections.singletonList(POSITIVE + "Boolean set!"), received);
        assertEquals(Boolean.FALSE, config.get("rightClickToViewCard"));
    }

    @Test
    public void theUsageReportingSwitchIsSetThroughItsDottedPath() {
        configService.saveConfigDefaults();
        config.set("usage-reporting.enabled", true);

        configService.setConfigOption("usage-reporting.enabled", "false", player);

        assertEquals(Collections.singletonList(POSITIVE + "Boolean set!"), received);
        assertEquals(Boolean.FALSE, config.get("usage-reporting.enabled"));
        assertFalse(YamlConfiguration.loadConfiguration(configFile).getBoolean("usage-reporting.enabled", true));
    }

    @Test
    public void anyOtherOptionIsStoredAsTheRawStringWithoutValidation() {
        configService.saveConfigDefaults();

        configService.setConfigOption("localChatColor", "pink", player);

        assertEquals(Collections.singletonList(POSITIVE + "String set!"), received);
        assertEquals("pink", config.get("localChatColor"));
    }

    @Test
    public void theConfirmationUsesTheNewlySetPositiveAlertColor() {
        configService.saveConfigDefaults();

        configService.setConfigOption("positiveAlertColor", "gold", player);

        assertEquals(Collections.singletonList(ChatColor.GOLD + "String set!"), received);
    }

    // Defaults

    @Test
    public void aFreshInstallGetsEveryDefaultWrittenToDisk() {
        configService.saveConfigDefaults();

        YamlConfiguration saved = YamlConfiguration.loadConfiguration(configFile);
        assertEquals(VERSION, saved.getString("version"));
        assertEquals(25, saved.getInt("localChatRadius"));
        assertEquals(2, saved.getInt("whisperChatRadius"));
        assertEquals(50, saved.getInt("yellChatRadius"));
        assertEquals(25, saved.getInt("emoteRadius"));
        assertEquals(300, saved.getInt("changeNameCooldown"));
        assertEquals("gray", saved.getString("localChatColor"));
        assertEquals("blue", saved.getString("whisperChatColor"));
        assertEquals("red", saved.getString("yellChatColor"));
        assertEquals("gray", saved.getString("emoteColor"));
        assertTrue(saved.getBoolean("rightClickToViewCard"));
        assertEquals(25, saved.getInt("localOOCChatRadius"));
        assertEquals("gray", saved.getString("localOOCChatColor"));
        assertEquals("green", saved.getString("positiveAlertColor"));
        assertEquals("aqua", saved.getString("neutralAlertColor"));
        assertEquals("red", saved.getString("negativeAlertColor"));
        assertTrue(saved.getBoolean("chatFeaturesEnabled"));
        assertFalse(saved.getBoolean("debugMode", true));
        assertEquals(20, saved.getInt("birdSpeed"));
        assertTrue(saved.getBoolean("logChat"));
        assertEquals(20, saved.getKeys(false).size());
    }

    @Test
    public void anUpgradeBackfillsMissingOptionsAndKeepsTheOperatorsValues() {
        config.set("version", "v1.0.0");
        config.set("localChatRadius", 40);
        config.set("emoteColor", "gold");

        configService.handleVersionMismatch();

        YamlConfiguration saved = YamlConfiguration.loadConfiguration(configFile);
        assertEquals(VERSION, saved.getString("version"));
        assertEquals(40, saved.getInt("localChatRadius"));
        assertEquals("gold", saved.getString("emoteColor"));
        assertEquals(50, saved.getInt("yellChatRadius"));
        assertEquals(20, saved.getInt("birdSpeed"));
    }

    @Test
    public void anUpgradeMovesTheMisspelledNeutralAlertColorToTheCorrectKey() {
        config.set("version", "v1.0.0");
        config.set("neurtalAlertColor", "gold");

        configService.handleVersionMismatch();

        YamlConfiguration saved = YamlConfiguration.loadConfiguration(configFile);
        assertEquals("gold", saved.getString("neutralAlertColor"));
        assertFalse(saved.isSet("neurtalAlertColor"));
    }

    @Test
    public void anUpgradeLeavesTheMisspelledKeyAloneWhenTheCorrectOneIsAlreadySet() {
        config.set("version", "v1.0.0");
        config.set("neutralAlertColor", "blue");
        config.set("neurtalAlertColor", "gold");

        configService.handleVersionMismatch();

        YamlConfiguration saved = YamlConfiguration.loadConfiguration(configFile);
        assertEquals("blue", saved.getString("neutralAlertColor"));
        assertEquals("gold", saved.getString("neurtalAlertColor"));
    }

    @Test
    public void anUpgradeDeletesTheObsoleteIntegerTestOptionOnly() {
        config.set("version", "v1.0.0");
        config.set("test", 3);

        configService.handleVersionMismatch();
        assertFalse(YamlConfiguration.loadConfiguration(configFile).isSet("test"));

        config.set("test", "three");
        configService.handleVersionMismatch();
        assertEquals("three", YamlConfiguration.loadConfiguration(configFile).getString("test"));
    }

    // Usage reporting

    @Test
    public void usageReportingFallsBackToTheBundledDefaultsWhenTheFileHasNoBlock() throws Exception {
        YamlConfiguration bundled = bundledConfig();
        config.setDefaults(bundled);

        assertTrue(configService.isUsageReportingEnabled());
        assertEquals(bundled.getString("usage-reporting.endpoint"), configService.getUsageReportingEndpoint());
        assertEquals(bundled.getString("usage-reporting.key"), configService.getUsageReportingKey());
    }

    @Test
    public void withNoBundledDefaultsReportingIsOffWithTheBuiltInEndpointAndAnEmptyKey() {
        assertFalse(configService.isUsageReportingEnabled());
        assertEquals("https://trace.danielstephenson.dev", configService.getUsageReportingEndpoint());
        assertEquals("", configService.getUsageReportingKey());
    }

    @Test
    public void theBundledUsageReportingBlockIsCopiedToAFileThatLacksIt() throws Exception {
        YamlConfiguration bundled = bundledConfig();
        config.setDefaults(bundled);
        config.set("localChatRadius", 40);

        configService.ensureUsageReportingBlockOnDisk();

        YamlConfiguration saved = YamlConfiguration.loadConfiguration(configFile);
        assertTrue(saved.getBoolean("usage-reporting.enabled"));
        assertEquals(bundled.getString("usage-reporting.endpoint"), saved.getString("usage-reporting.endpoint"));
        assertEquals(bundled.getString("usage-reporting.key"), saved.getString("usage-reporting.key"));
        assertEquals(40, saved.getInt("localChatRadius"));
    }

    @Test
    public void anExistingUsageReportingBlockIsLeftAlone() throws Exception {
        config.setDefaults(bundledConfig());
        config.set("usage-reporting.enabled", false);

        configService.ensureUsageReportingBlockOnDisk();

        assertFalse(configFile.exists());
        assertFalse(config.isSet("usage-reporting.key"));
        assertFalse(configService.isUsageReportingEnabled());
    }

    @Test
    public void withNoBundledDefaultsNothingIsCopied() {
        configService.ensureUsageReportingBlockOnDisk();

        assertFalse(configFile.exists());
    }

    private static YamlConfiguration bundledConfig() throws Exception {
        try (InputStream stream = ConfigServiceTest.class.getClassLoader().getResourceAsStream("config.yml")) {
            return YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8));
        }
    }

    /**
     * Allocates the plugin without running {@code JavaPlugin}'s constructor, then sets what {@link ConfigService} reads:
     * {@code getConfig()} returns {@code config} and {@code saveConfig()} writes it to {@code configFile}.
     */
    private static MedievalRoleplayEngine pluginWith(FileConfiguration config, File configFile) throws Exception {
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Object unsafe = theUnsafe.get(null);
        Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
        MedievalRoleplayEngine plugin =
                (MedievalRoleplayEngine) allocateInstance.invoke(unsafe, MedievalRoleplayEngine.class);
        setField(MedievalRoleplayEngine.class, plugin, "pluginVersion", VERSION);
        setField(MedievalRoleplayEngine.class, plugin, "colorChecker", new ColorChecker(plugin));
        setField(JavaPlugin.class, plugin, "newConfig", config);
        setField(JavaPlugin.class, plugin, "configFile", configFile);
        return plugin;
    }

    private static void setField(Class<?> owner, Object target, String name, Object value) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    /** A player that records every message it is sent. */
    private static Player player(final List<String> received) {
        return (Player) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class<?>[]{Player.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        switch (method.getName()) {
                            case "sendMessage":
                                received.add((String) args[0]);
                                return null;
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
}
