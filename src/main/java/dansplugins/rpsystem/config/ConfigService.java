package dansplugins.rpsystem.config;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ConfigService {
    private final MedievalRoleplayEngine medievalRoleplayEngine;
    private boolean altered = false;

    public ConfigService(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    public void handleVersionMismatch() {
        // set version
        if (!getConfig().isString("version")) {
            getConfig().addDefault("version", medievalRoleplayEngine.getVersion());
        }
        else {
            getConfig().set("version", medievalRoleplayEngine.getVersion());
        }

        // add defaults if they don't exist
        if (!getConfig().isInt("localChatRadius")) {
            getConfig().addDefault("localChatRadius", 25);
        }
        if (!getConfig().isInt("whisperChatRadius")) {
            getConfig().addDefault("whisperChatRadius", 2);
        }
        if (!getConfig().isInt("yellChatRadius")) {
            getConfig().addDefault("yellChatRadius", 50);
        }
        if (!getConfig().isInt("emoteRadius")) {
            getConfig().addDefault("emoteRadius", 25);
        }
        if (!getConfig().isInt("changeNameCooldown")) {
            getConfig().addDefault("changeNameCooldown", 300);
        }
        if (!getConfig().isString("localChatColor")) {
            getConfig().addDefault("localChatColor", "gray");
        }
        if (!getConfig().isString("whisperChatColor")) {
            getConfig().addDefault("whisperChatColor", "blue");
        }
        if (!getConfig().isString("yellChatColor")) {
            getConfig().addDefault("yellChatColor", "red");
        }
        if (!getConfig().isString("emoteColor")) {
            getConfig().addDefault("emoteColor", "yellow");
        }
        if (!getConfig().isBoolean("rightClickToViewCard")) {
            getConfig().addDefault("rightClickToViewCard", true);
        }
        if (!getConfig().isInt("localOOCChatRadius")) {
            getConfig().addDefault("localOOCChatRadius", 25);
        }
        if (!getConfig().isString("localOOCChatColor")) {
            getConfig().addDefault("localOOCChatColor", "gray");
        }
        if (!getConfig().isString("positiveAlertColor")) {
            getConfig().addDefault("positiveAlertColor", "green");
        }
        if (!getConfig().isString("neurtalAlertColor")) {
            getConfig().addDefault("neutralAlertColor", "aqua");
        }
        if (!getConfig().isString("negativeAlertColor")) {
            getConfig().addDefault("negativeAlertColor", "red");
        }
        if (!getConfig().isString("chatFeaturesEnabled")) {
            getConfig().addDefault("chatFeaturesEnabled", true);
        }
        if (!getConfig().isBoolean("debugMode")) {
            getConfig().addDefault("debugMode", false);
        }
        if (!getConfig().isInt("birdSpeed")) {
            getConfig().addDefault("birdSpeed", 20);
        }
        if (!getConfig().isBoolean("logChat")) {
            getConfig().addDefault("logChat", false);
        }
        
        deleteOldConfigOptionsIfPresent();

        getConfig().options().copyDefaults(true);
        medievalRoleplayEngine.saveConfig();
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
                getConfig().set(option, Integer.parseInt(value));
                player.sendMessage(medievalRoleplayEngine.colorChecker.getColorByName(getString("positiveAlertColor")) + "Integer set!");
            }
            else if (option.equalsIgnoreCase("rightClickToViewCard")
                    || option.equalsIgnoreCase("chatFeaturesEnabled")
                    || option.equalsIgnoreCase("debugMode")
                    || option.equalsIgnoreCase("logChat")) {
                getConfig().set(option, Boolean.parseBoolean(value));
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

            // save
            medievalRoleplayEngine.saveConfig();
            altered = true;
        }
        else {
            player.sendMessage(ChatColor.RED + String.format("The option '%s' wasn't found.", option));
        }

    }

    public void saveConfigDefaults() {
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
        getConfig().options().copyDefaults(true);
        medievalRoleplayEngine.saveConfig();
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
                + ", logChat:" + getConfig().getBoolean("logChat"));
    }

    public boolean hasBeenAltered() {
        return altered;
    }

    public FileConfiguration getConfig() {
        return medievalRoleplayEngine.getConfig();
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
