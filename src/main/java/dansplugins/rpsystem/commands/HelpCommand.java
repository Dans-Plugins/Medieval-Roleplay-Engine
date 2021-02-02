package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.MedievalRoleplayEngine;
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

            player.sendMessage(ChatColor.AQUA + " == Medieval Roleplay Engine " + MedievalRoleplayEngine.getInstance().getVersion() + " Commands == ");
            player.sendMessage(ChatColor.AQUA + "/rphelp - Show a list of useful commands for the plugin.");
            player.sendMessage(ChatColor.AQUA + "/card help - Show a list of commands useful for managing character cards.");
            player.sendMessage(ChatColor.AQUA + "/bird - Send a bird to another player.");
            player.sendMessage(ChatColor.AQUA + "/local or /rp - Enter local chat.");
            player.sendMessage(ChatColor.AQUA + "/global or /ooc - Enter global chat.");
            player.sendMessage(ChatColor.AQUA + "/emote or /me - Send an emote to nearby players.");
            player.sendMessage(ChatColor.AQUA + "/roll or /dice - Roll a dice.");
            player.sendMessage(ChatColor.AQUA + "/title - Rename an unwritten book.");
            player.sendMessage(ChatColor.AQUA + "/yell - Send a single messages to far away players.");
            player.sendMessage(ChatColor.AQUA + "/whisper - Send a single message to very close players.");
            player.sendMessage(ChatColor.AQUA + "/rpconfig - View and change config options.");

        }
        else {
            player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need the following permission: 'rp.rphelp'");
        }

    }

}
