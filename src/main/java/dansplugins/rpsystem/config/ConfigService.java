package dansplugins.rpsystem.config;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class ConfigService {
    private static final String USAGE_REPORTING_ENABLED_KEY = UsageReportingSetting.ENABLED_KEY;
    private static final String USAGE_REPORTING_ENDPOINT_KEY = "usage-reporting.endpoint";
    private static final String USAGE_REPORTING_KEY_KEY = "usage-reporting.key";
    private static final String DEFAULT_USAGE_REPORTING_ENDPOINT = "https://trace.danielstephenson.dev";

    private final MedievalRoleplayEngine medievalRoleplayEngine;
    // Set when /rpconfig set usage-reporting.enabled was used: that command, and only that one,
    // may change the switch away from what config.yml on disk says.
    private boolean usageReportingSetByCommand = false;
    private boolean altered = false;

    public ConfigService(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    public void handleVersionMismatch() {
        // An existing version string is overwritten with the running version rather than merely defaulted.
        // Where none is present, the default registered by addConfigDefaults() supplies it instead.
        if (getConfig().isString("version")) {
            getConfig().set("version", medievalRoleplayEngine.getVersion());
        }

        // Must run before addConfigDefaults(), because isString() falls back to the defaults section:
        // once a default is registered for neutralAlertColor, the migration below can never fire.
        migrateMisspelledNeutralAlertColorOption();

        addConfigDefaults();

        deleteOldConfigOptionsIfPresent();

        getConfig().options().copyDefaults(true);
        saveConfigKeepingUsageReportingOptOut();
    }

    private void migrateMisspelledNeutralAlertColorOption() {
        if (!getConfig().isString("neutralAlertColor") && getConfig().isString("neurtalAlertColor")) {
            getConfig().set("neutralAlertColor", getConfig().getString("neurtalAlertColor"));
            getConfig().set("neurtalAlertColor", null);
        }
    }

    /**
     * Registers the default value of every configuration option.
     * <p>
     * This is the single source of truth for the defaults, shared by the fresh-install path
     * ({@link #saveConfigDefaults()}) and the upgrade path ({@link #handleVersionMismatch()}),
     * so a new or retuned option only has to be declared once.
     * <p>
     * addDefault() records a default rather than a value, and copyDefaults(true) only writes
     * defaults for options that are not already set, so calling this over an existing config
     * backfills the missing options without disturbing the ones the server operator has set.
     */
    private void addConfigDefaults() {
        getConfig().addDefault("version", medievalRoleplayEngine.getVersion());
        getConfig().addDefault("localChatRadius", 25);
        getConfig().addDefault("whisperChatRadius", 2);
        getConfig().addDefault("yellChatRadius", 50);
        getConfig().addDefault("emoteRadius", 25);
        getConfig().addDefault("changeNameCooldown", 300);
        getConfig().addDefault("localChatColor", "gray");
        getConfig().addDefault("whisperChatColor", "blue");
        getConfig().addDefault("yellChatColor", "red");
        getConfig().addDefault("emoteColor", "gray");
        getConfig().addDefault("rightClickToViewCard", true);
        getConfig().addDefault("localOOCChatRadius", 25);
        getConfig().addDefault("localOOCChatColor", "gray");
        getConfig().addDefault("positiveAlertColor", "green");
        getConfig().addDefault("neutralAlertColor", "aqua");
        getConfig().addDefault("negativeAlertColor", "red");
        getConfig().addDefault("chatFeaturesEnabled", true);
        getConfig().addDefault("debugMode", false);
        getConfig().addDefault("birdSpeed", 20);
        getConfig().addDefault("logChat", true);
    }

    private void deleteOldConfigOptionsIfPresent() {

        if (getConfig().isInt("test")) {
            getConfig().set("test", null);
        }

    }

    public void setConfigOption(String option, String value, Player player) {

        if (getConfig().isSet(option)) {

            if (option.equalsIgnoreCase("version")) {
                player.sendMessage(ChatColor.RED + "Cannot set version!");
                return;
            }
            else if (option.equalsIgnoreCase("localChatRadius")
                    || option.equalsIgnoreCase("whisperChatRadius")
                    || option.equalsIgnoreCase("yellChatRadius")
                    || option.equalsIgnoreCase("changeNameCooldown")
                    || option.equalsIgnoreCase("emoteRadius")
                    || option.equalsIgnoreCase("localOOCChatRadius")
                    || option.equalsIgnoreCase("birdSpeed")) {
                int parsed;
                try {
                    parsed = Integer.parseInt(value);
                }
                catch (NumberFormatException e) {
                    player.sendMessage(medievalRoleplayEngine.colorChecker.getColorByName(getString("negativeAlertColor")) + option + " must be a whole number.");
                    return;
                }
                getConfig().set(option, parsed);
                player.sendMessage(medievalRoleplayEngine.colorChecker.getColorByName(getString("positiveAlertColor")) + "Integer set!");
            }
            else if (option.equalsIgnoreCase("rightClickToViewCard")
                    || option.equalsIgnoreCase("chatFeaturesEnabled")
                    || option.equalsIgnoreCase("debugMode")
                    || option.equalsIgnoreCase("logChat")
                    || option.equalsIgnoreCase(USAGE_REPORTING_ENABLED_KEY)) {
                getConfig().set(option, Boolean.parseBoolean(value));
                if (option.equalsIgnoreCase(USAGE_REPORTING_ENABLED_KEY)) {
                    usageReportingSetByCommand = true;
                }
                player.sendMessage(medievalRoleplayEngine.colorChecker.getColorByName(getString("positiveAlertColor")) + "Boolean set!");
            }
            else if (option.equalsIgnoreCase("doubletest")) {
                getConfig().set(option, Double.parseDouble(value));
                player.sendMessage(medievalRoleplayEngine.colorChecker.getColorByName(getString("positiveAlertColor")) + "Double set!");
            }
            else {
                getConfig().set(option, value);
                player.sendMessage(medievalRoleplayEngine.colorChecker.getColorByName(getString("positiveAlertColor")) + "String set!");
            }

            saveConfigKeepingUsageReportingOptOut();
            altered = true;
        }
        else {
            player.sendMessage(ChatColor.RED + String.format("The option '%s' wasn't found.", option));
        }

    }

    public void saveConfigDefaults() {
        addConfigDefaults();
        getConfig().options().copyDefaults(true);
        saveConfigKeepingUsageReportingOptOut();
    }

    public void sendPlayerConfigList(Player player) {
        player.sendMessage(medievalRoleplayEngine.colorChecker.getColorByName(getString("neutralAlertColor")) + "version: " + getConfig().getString("version")
                + ", debugMode: " + getConfig().getBoolean("debugMode")
                + ", chatFeaturesEnabled: " + getConfig().getBoolean("chatFeaturesEnabled")
                + ", localChatRadius: " + getConfig().getInt("localChatRadius")
                + ", whisperChatRadius: " + getConfig().getInt("whisperChatRadius")
                + ", yellChatRadius: " + getConfig().getInt("yellChatRadius")
                + ", emoteRadius: " + getConfig().getInt("emoteRadius")
                + ", changeNameCooldown: " + getConfig().getInt("changeNameCooldown")
                + ", localChatColor: " + getConfig().getString("localChatColor")
                + ", whisperChatColor: " + getConfig().getString("whisperChatColor")
                + ", yellChatColor: " + getConfig().getString("yellChatColor")
                + ", emoteColor: " + getConfig().getString("emoteColor")
                + ", rightClickToViewCard: " + getConfig().getBoolean("rightClickToViewCard")
                + ", localOOCChatRadius: " + getConfig().getInt("localOOCChatRadius")
                + ", localOOCChatColor: " + getConfig().getString("localOOCChatColor")
                + ", positiveAlertColor: " + getConfig().getString("positiveAlertColor")
                + ", neutralAlertColor: " + getConfig().getString("neutralAlertColor")
                + ", negativeAlertColor: " + getConfig().getString("negativeAlertColor")
                + ", birdSpeed: " + getConfig().getString("birdSpeed")
                + ", logChat:" + getConfig().getBoolean("logChat")
                + ", usage-reporting.enabled: " + isUsageReportingEnabled());
    }

    public boolean hasBeenAltered() {
        return altered;
    }

    public FileConfiguration getConfig() {
        return medievalRoleplayEngine.getConfig();
    }

    /**
     * Copies the bundled usage-reporting block into the server's config.yml when the file on
     * disk has none, so the opt-out is visible where an operator would look for it. The two
     * save paths (fresh install and version change) already write it through copyDefaults(true);
     * this covers the remaining case, a config.yml generated by a build that predates usage
     * reporting and carries the same version string. The values come from the defaults Bukkit
     * registered from the jar's config.yml (never new literals), and a file that already has the
     * block is left alone.
     */
    public void ensureUsageReportingBlockOnDisk() {
        if (getConfig().isSet("usage-reporting") || getConfig().getDefaults() == null) {
            return;
        }
        for (String key : new String[] {USAGE_REPORTING_ENABLED_KEY, USAGE_REPORTING_ENDPOINT_KEY, USAGE_REPORTING_KEY_KEY}) {
            getConfig().set(key, getConfig().getDefaults().get(key));
        }
        saveConfigKeepingUsageReportingOptOut();
    }

    // The usage-reporting options are deliberately NOT declared in addConfigDefaults(). Those
    // defaults are only registered on a fresh install or when the version string changes, and
    // reloadConfig() discards them again, so a config.yml that predates the block but carries the
    // current version would never resolve them. They live in the jar's config.yml instead: Bukkit
    // registers that file as the defaults for config.yml on every load, and the one-argument
    // getters below fall through to it -- but the two-argument getters (getString(path, def))
    // return their explicit fallback instead, which for the key would be "" and would turn
    // reporting off on a config.yml that has no usage-reporting block yet. Verified against
    // YamlConfiguration, not assumed. (copyDefaults(true) in the two save paths, and
    // ensureUsageReportingBlockOnDisk() on every other enable, write the block into config.yml
    // so operators can see and edit it; the fall-through only matters until that has run once.)

    /**
     * Whether usage reporting is switched on. Fails closed: reporting is on only when both the
     * loaded config and config.yml as it is on disk right now allow it, and only a value that
     * clearly means "on" allows it (see {@link UsageReportingSetting#allowsReporting(Object)}).
     * The one-argument get() falls through to the jar's defaults when config.yml has no block.
     */
    public boolean isUsageReportingEnabled() {
        if (!usageReportingSetByCommand && UsageReportingSetting.turnedOffOnDisk(configFile())) {
            return false;
        }
        return UsageReportingSetting.allowsReporting(getConfig().get(USAGE_REPORTING_ENABLED_KEY));
    }

    /**
     * Writes the loaded config to config.yml without ever turning usage reporting back on. The
     * loaded config can be older than the file -- an operator may have set enabled: false by hand
     * while the server was running -- and a plain saveConfig() would write the stale value over
     * theirs. So an opt-out on disk is carried into the loaded config first, unless
     * /rpconfig set usage-reporting.enabled has changed the switch since. A config.yml that cannot
     * be parsed is never written over.
     */
    void saveConfigKeepingUsageReportingOptOut() {
        if (UsageReportingSetting.isUnparseable(configFile())) {
            // Bukkit loaded it as empty: saving would replace every setting, the opt-out included,
            // with the defaults.
            java.util.logging.Logger logger = medievalRoleplayEngine.getLogger() != null
                    ? medievalRoleplayEngine.getLogger() : java.util.logging.Logger.getLogger("MedievalRoleplayEngine");
            logger.severe("config.yml could not be read, so it was not overwritten."
                    + " Fix the YAML error in it and restart; until then usage reporting is off.");
            return;
        }
        if (!usageReportingSetByCommand) {
            UsageReportingSetting.keepOptOutFromDisk(getConfig(), configFile());
        }
        medievalRoleplayEngine.saveConfig();
    }

    private File configFile() {
        return new File(medievalRoleplayEngine.getDataFolder(), "config.yml");
    }

    public String getUsageReportingEndpoint() {
        String endpoint = getConfig().getString(USAGE_REPORTING_ENDPOINT_KEY);
        return endpoint != null ? endpoint : DEFAULT_USAGE_REPORTING_ENDPOINT;
    }

    /** Empty when no key is configured or bundled, which the client treats as "off". */
    public String getUsageReportingKey() {
        String key = getConfig().getString(USAGE_REPORTING_KEY_KEY);
        return key != null ? key : "";
    }

    public int getInt(String option) {
        return getConfig().getInt(option);
    }

    public boolean getBoolean(String option) {
        return getConfig().getBoolean(option);
    }

    public double getDouble(String option) {
        return getConfig().getDouble(option);
    }

    public String getString(String option) {
        return getConfig().getString(option);
    }

}
