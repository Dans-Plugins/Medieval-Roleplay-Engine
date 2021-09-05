package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.ConfigManager;
import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpCommand {

    public void showListOfCommands(CommandSender sender) {

        // player check
        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        if (player.hasPermission("rp.rphelp") || player.hasPermission("rp.default")) {

            player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + " == Medieval Roleplay Engine " + MedievalRoleplayEngine.getInstance().getVersion() + " Commands == ");
            player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "/rphelp - Show a list of useful commands for the plugin.");
            player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "/card help - Show a list of commands useful for managing character cards.");
            player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "/bird - Send a bird to another player.");
            player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "/rp - Enter local (RP) chat.");
            player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "/ooc - Enter global (OOC) chat.");
            player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "/emote or /me - Send an emote to nearby players.");
            player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "/roll or /dice - Roll a dice.");
            player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "/title - Rename an unwritten book.");
            player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "/yell (message)- Send a single messages to far away players.");
            player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "/whisper (message) - Send a single message to very close players.");
            player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "/lo (message) - Send an single OOC message to nearby players.");
            player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "/rpconfig - View and change config options.");

        }
        else {
            player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need the following permission: 'rp.rphelp'");
        }

    }

}
