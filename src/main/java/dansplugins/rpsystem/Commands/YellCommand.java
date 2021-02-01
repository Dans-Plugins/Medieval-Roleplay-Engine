package dansplugins.rpsystem.Commands;

import dansplugins.rpsystem.ColorChecker;
import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class YellCommand {

    public void sendLoudMessage(CommandSender sender, String[] args) {

        int yellChatRadius = MedievalRoleplayEngine.getInstance().getConfig().getInt("yellChatRadius");
        String yellChatColor =MedievalRoleplayEngine.getInstance().getConfig().getString("yellChatColor");

        // player check
        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        if (player.hasPermission("rp.yell") || player.hasPermission("rp.default")) {

            if (args.length > 0) {
                String message = ColorChecker.getInstance().getColorByName(yellChatColor) + "" + String.format("%s yells: \"%s\"", Utilities.getInstance().getCard(player.getUniqueId()).getName(), Utilities.getInstance().createStringFromArgs(args));

                Utilities.sendMessageToPlayersWithinDistance(player, message, yellChatRadius);
            }
            else {
                player.sendMessage(ChatColor.RED + "Usage: /yell (message)");
            }

        }
        else {
            player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need the following permission: 'rp.yell'");
        }

    }

}
