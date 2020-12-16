package rpsystem.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rpsystem.MedievalRoleplayEngine;
import rpsystem.Utilities;

import static rpsystem.Utilities.sendMessageToPlayersWithinDistance;

public class WhisperCommand {

    MedievalRoleplayEngine medievalRoleplayEngine = null;

    public WhisperCommand(MedievalRoleplayEngine plugin) {
        medievalRoleplayEngine = plugin;
    }

    public void sendQuietMessage(CommandSender sender, String[] args) {
        // player check
        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        if (player.hasPermission("rp.whisper") || player.hasPermission("rp.default")) {

            if (args.length > 0) {
                String message = ChatColor.BLUE + "" + String.format("%s whispers: \"%s\"", Utilities.getInstance().getCard(player.getUniqueId()).getName(), Utilities.getInstance().createStringFromArgs(args));

                int numPlayersWhoHeard = sendMessageToPlayersWithinDistance(player, message, 2);

                player.sendMessage(ChatColor.AQUA + "" + numPlayersWhoHeard + " players heard your whisper.");
            }
            else {
                player.sendMessage(ChatColor.RED + "Usage: /whisper (message)");
            }

        }
        else {
            player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need the following permission: 'rp.whisper'");
        }

    }

}
