package rpsystem;

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
                    option.equalsIgnoreCase("yellChatRadius")) {
                MedievalRoleplayEngine.getInstance().getConfig().set(option, Integer.parseInt(value));
                player.sendMessage(ChatColor.GREEN + "Integer set!");
            }
            else if (option.equalsIgnoreCase("booleantest")) {
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
        MedievalRoleplayEngine.getInstance().getConfig().options().copyDefaults(true);
        MedievalRoleplayEngine.getInstance().saveConfig();
    }

    public void sendPlayerConfigList(Player player) {
        player.sendMessage(ChatColor.AQUA + "version: " + MedievalRoleplayEngine.getInstance().getConfig().getString("version")
                + ", localChatRadius: " + MedievalRoleplayEngine.getInstance().getConfig().getInt("localChatRadius")
                + ", whisperChatRadius: " + MedievalRoleplayEngine.getInstance().getConfig().getInt("whisperChatRadius")
                + ", yellChatRadius: " + MedievalRoleplayEngine.getInstance().getConfig().getInt("yellChatRadius"));
    }

}
