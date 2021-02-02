package dansplugins.rpsystem;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ConfigManager {

    private static ConfigManager instance;

    private ConfigManager() {

    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public void handleVersionMismatch() {
        // set version
        if (!MedievalRoleplayEngine.getInstance().getConfig().isString("version")) {
            MedievalRoleplayEngine.getInstance().getConfig().addDefault("version", MedievalRoleplayEngine.getInstance().getVersion());
        }
        else {
            MedievalRoleplayEngine.getInstance().getConfig().set("version", MedievalRoleplayEngine.getInstance().getVersion());
        }

        // add defaults if they don't exist
        if (!MedievalRoleplayEngine.getInstance().getConfig().isInt("localChatRadius")) {
            MedievalRoleplayEngine.getInstance().getConfig().addDefault("localChatRadius", 25);
        }

        if (!MedievalRoleplayEngine.getInstance().getConfig().isInt("whisperChatRadius")) {
            MedievalRoleplayEngine.getInstance().getConfig().addDefault("whisperChatRadius", 2);
        }

        if (!MedievalRoleplayEngine.getInstance().getConfig().isInt("yellChatRadius")) {
            MedievalRoleplayEngine.getInstance().getConfig().addDefault("yellChatRadius", 50);
        }

        if (!MedievalRoleplayEngine.getInstance().getConfig().isInt("emoteRadius")) {
            MedievalRoleplayEngine.getInstance().getConfig().addDefault("emoteRadius", 25);
        }

        if (!MedievalRoleplayEngine.getInstance().getConfig().isInt("changeNameCooldown")) {
            MedievalRoleplayEngine.getInstance().getConfig().addDefault("changeNameCooldown", 300);
        }

        if (!MedievalRoleplayEngine.getInstance().getConfig().isString("localChatColor")) {
            MedievalRoleplayEngine.getInstance().getConfig().addDefault("localChatColor", "gray");
        }

        if (!MedievalRoleplayEngine.getInstance().getConfig().isString("whisperChatColor")) {
            MedievalRoleplayEngine.getInstance().getConfig().addDefault("whisperChatColor", "blue");
        }

        if (!MedievalRoleplayEngine.getInstance().getConfig().isString("yellChatColor")) {
            MedievalRoleplayEngine.getInstance().getConfig().addDefault("yellChatColor", "red");
        }

        if (!MedievalRoleplayEngine.getInstance().getConfig().isString("emoteColor")) {
            MedievalRoleplayEngine.getInstance().getConfig().addDefault("emoteColor", "gray");
        }

        if (!MedievalRoleplayEngine.getInstance().getConfig().isBoolean("rightClickToViewCard")) {
            MedievalRoleplayEngine.getInstance().getConfig().addDefault("rightClickToViewCard", true);
        }

        deleteOldConfigOptionsIfPresent();

        MedievalRoleplayEngine.getInstance().getConfig().options().copyDefaults(true);
        MedievalRoleplayEngine.getInstance().saveConfig();
    }

    private void deleteOldConfigOptionsIfPresent() {

        if (MedievalRoleplayEngine.getInstance().getConfig().isInt("test")) {
            MedievalRoleplayEngine.getInstance().getConfig().set("test", null);
        }

    }

    public static void setConfigOption(String option, String value, Player player) {

        if (MedievalRoleplayEngine.getInstance().getConfig().isSet(option)) {

            if (option.equalsIgnoreCase("version")) {
                player.sendMessage(ChatColor.RED + "Cannot set version!");
                return;
            }
            else if (option.equalsIgnoreCase("localChatRadius") ||
                    option.equalsIgnoreCase("whisperChatRadius") ||
                    option.equalsIgnoreCase("yellChatRadius") ||
                    option.equalsIgnoreCase("changeNameCooldown") ||
                    option.equalsIgnoreCase("emoteRadius")) {
                MedievalRoleplayEngine.getInstance().getConfig().set(option, Integer.parseInt(value));
                player.sendMessage(ChatColor.GREEN + "Integer set!");
            }
            else if (option.equalsIgnoreCase("rightClickToViewCard")) {
                MedievalRoleplayEngine.getInstance().getConfig().set(option, Boolean.parseBoolean(value));
                player.sendMessage(ChatColor.GREEN + "Boolean set!");
            }
            else if (option.equalsIgnoreCase("doubletest")) {
                MedievalRoleplayEngine.getInstance().getConfig().set(option, Double.parseDouble(value));
                player.sendMessage(ChatColor.GREEN + "Double set!");
            }
            else {
                MedievalRoleplayEngine.getInstance().getConfig().set(option, value);
                player.sendMessage(ChatColor.GREEN + "String set!");
            }

            // save
            MedievalRoleplayEngine.getInstance().saveConfig();
        }
        else {
            player.sendMessage(ChatColor.RED + String.format("The option '%s' wasn't found.", option));
        }

    }

    public void saveConfigDefaults() {
        MedievalRoleplayEngine.getInstance().getConfig().addDefault("version", MedievalRoleplayEngine.getInstance().getVersion());
        MedievalRoleplayEngine.getInstance().getConfig().addDefault("localChatRadius", 25);
        MedievalRoleplayEngine.getInstance().getConfig().addDefault("whisperChatRadius", 2);
        MedievalRoleplayEngine.getInstance().getConfig().addDefault("yellChatRadius", 50);
        MedievalRoleplayEngine.getInstance().getConfig().addDefault("emoteRadius", 25);
        MedievalRoleplayEngine.getInstance().getConfig().addDefault("changeNameCooldown", 300);
        MedievalRoleplayEngine.getInstance().getConfig().addDefault("localChatColor", "gray");
        MedievalRoleplayEngine.getInstance().getConfig().addDefault("whisperChatColor", "blue");
        MedievalRoleplayEngine.getInstance().getConfig().addDefault("yellChatColor", "red");
        MedievalRoleplayEngine.getInstance().getConfig().addDefault("emoteColor", "gray");
        MedievalRoleplayEngine.getInstance().getConfig().addDefault("rightClickToViewCard", true);
        MedievalRoleplayEngine.getInstance().getConfig().options().copyDefaults(true);
        MedievalRoleplayEngine.getInstance().saveConfig();
    }

    public void sendPlayerConfigList(Player player) {
        player.sendMessage(ChatColor.AQUA + "version: " + MedievalRoleplayEngine.getInstance().getConfig().getString("version")
                + ", localChatRadius: " + MedievalRoleplayEngine.getInstance().getConfig().getInt("localChatRadius")
                + ", whisperChatRadius: " + MedievalRoleplayEngine.getInstance().getConfig().getInt("whisperChatRadius")
                + ", yellChatRadius: " + MedievalRoleplayEngine.getInstance().getConfig().getInt("yellChatRadius")
                + ", emoteRadius: " + MedievalRoleplayEngine.getInstance().getConfig().getInt("emoteRadius")
                + ", changeNameCooldown: " + MedievalRoleplayEngine.getInstance().getConfig().getInt("changeNameCooldown")
                + ", localChatColor: " + MedievalRoleplayEngine.getInstance().getConfig().getString("localChatColor")
                + ", whisperChatColor: " + MedievalRoleplayEngine.getInstance().getConfig().getString("whisperChatColor")
                + ", yellChatColor: " + MedievalRoleplayEngine.getInstance().getConfig().getString("yellChatColor")
                + ", emoteColor: " + MedievalRoleplayEngine.getInstance().getConfig().getString("emoteColor")
                + ", rightClickToViewCard: " + MedievalRoleplayEngine.getInstance().getConfig().getBoolean("rightClickToViewCard"));
    }

}
