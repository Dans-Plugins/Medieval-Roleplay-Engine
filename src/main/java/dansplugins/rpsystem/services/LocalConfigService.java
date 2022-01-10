package dansplugins.rpsystem.services;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author Daniel McCoy Stephenson
 *     To add a new config option, the following methods must be altered:
 *     - saveMissingConfigDefaultsIfNotPresent
 *     - setConfigOption()
 *     - sendConfigList()
 *     
 *     To Add:
 *         configOptions.put("version", getVersion());
 *         configOptions.put("debugMode", false);
 *         configOptions.put("localChatRadius", 25);
 *         configOptions.put("whisperChatRadius", 2);
 *         configOptions.put("yellChatRadius", 50);
 *         configOptions.put("emoteRadius", 25);
 *         configOptions.put("changeNameCooldown", 300);
 *         configOptions.put("localChatColor", "gray");
 *         configOptions.put("whisperChatColor", "blue");
 *         configOptions.put("yellChatColor", "red");
 *         configOptions.put("emoteColor", "gray");
 *         configOptions.put("rightClickToViewCard", true);
 *         configOptions.put("localOOCChatRadius", 25);
 *         configOptions.put("localOOCChatColor", "gray");
 *         configOptions.put("positiveAlertColor", "green");
 *         configOptions.put("neutralAlertColor", "aqua");
 *         configOptions.put("negativeAlertColor", "red");
 *         configOptions.put("chatFeaturesEnabled", true);
 *         configOptions.put("legacyChat", false);
 *         configOptions.put("preventSelfBirding", true);
 */
public class LocalConfigService {
    private static LocalConfigService instance;
    private boolean altered = false;

    private LocalConfigService() {
        
    }

    public static LocalConfigService getInstance() {
        if (instance == null) {
            instance = new LocalConfigService();
        }
        return instance;
    }

    public void saveMissingConfigDefaultsIfNotPresent() {
        // set version
        if (!getConfig().isString("version")) {
            getConfig().addDefault("version", MedievalRoleplayEngine.getInstance().getVersion());
        } else {
            getConfig().set("version", MedievalRoleplayEngine.getInstance().getVersion());
        }

        // save config options
        if (!isSet("debugMode")) { getConfig().set("debugMode", false); }

        getConfig().options().copyDefaults(true);
        MedievalRoleplayEngine.getInstance().saveConfig();
    }

    public void setConfigOption(String option, String value, CommandSender sender) {
        if (getConfig().isSet(option)) {
            if (option.equalsIgnoreCase("version")) {
                sender.sendMessage(ChatColor.RED + "Cannot set version.");
                return;
            } else if (option.equalsIgnoreCase("A")) {
                getConfig().set(option, Integer.parseInt(value));
                sender.sendMessage(ChatColor.GREEN + "Integer set.");
            } else if (option.equalsIgnoreCase("debugMode")) {
                getConfig().set(option, Boolean.parseBoolean(value));
                sender.sendMessage(ChatColor.GREEN + "Boolean set.");
            } else if (option.equalsIgnoreCase("C")) {
                getConfig().set(option, Double.parseDouble(value));
                sender.sendMessage(ChatColor.GREEN + "Double set.");
            } else {
                getConfig().set(option, value);
                sender.sendMessage(ChatColor.GREEN + "String set.");
            }

            // save
            MedievalRoleplayEngine.getInstance().saveConfig();
            altered = true;
        } else {
            sender.sendMessage(ChatColor.RED + "That config option wasn't found.");
        }
    }

    public void sendConfigList(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== Config List ===");
        sender.sendMessage(ChatColor.AQUA + "version: " + getConfig().getString("version")
                + ", debugMode: " + getString("debugMode"));
    }

    public boolean hasBeenAltered() {
        return altered;
    }

    public FileConfiguration getConfig() {
        return MedievalRoleplayEngine.getInstance().getConfig();
    }

    public boolean isSet(String option) {
        return getConfig().isSet(option);
    }

    public int getInt(String option) {
        return getConfig().getInt(option);
    }

    public int getIntOrDefault(String option, int defaultValue) {
        int toReturn = getInt(option);
        if (toReturn == 0) {
            return defaultValue;
        }
        return toReturn;
    }

    public boolean getBoolean(String option) {
        return getConfig().getBoolean(option);
    }

    public double getDouble(String option) {
        return getConfig().getDouble(option);
    }

    public double getDoubleOrDefault(String option, double defaultValue) {
        double toReturn = getDouble(option);
        if (toReturn == 0) {
            return defaultValue;
        }
        return toReturn;
    }

    public String getString(String option) {
        return getConfig().getString(option);
    }

    public String getStringOrDefault(String option, String defaultValue) {
        String toReturn = getString(option);
        if (toReturn == null) {
            return defaultValue;
        }
        return toReturn;
    }
}