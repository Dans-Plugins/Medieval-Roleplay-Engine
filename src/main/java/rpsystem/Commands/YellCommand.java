package rpsystem.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rpsystem.Utilities;

import static rpsystem.Utilities.sendMessageToPlayersWithinDistance;

public class YellCommand {

    public void sendLoudMessage(CommandSender sender, String[] args) {
        // player check
        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        if (player.hasPermission("rp.yell") || player.hasPermission("rp.default")) {

            if (args.length > 0) {
                String message = ChatColor.RED + "" + String.format("%s yells: \"%s\"", Utilities.getInstance().getCard(player.getUniqueId()).getName(), Utilities.getInstance().createStringFromArgs(args));

                sendMessageToPlayersWithinDistance(player, message, 50);
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
