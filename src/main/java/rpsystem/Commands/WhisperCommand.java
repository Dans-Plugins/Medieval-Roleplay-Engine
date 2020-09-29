package rpsystem.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rpsystem.Main;
import rpsystem.Subsystems.UtilitySubsystem;

import static rpsystem.Subsystems.UtilitySubsystem.sendMessageToPlayersWithinDistance;

public class WhisperCommand {

    Main main = null;

    public WhisperCommand(Main plugin) {
        main = plugin;
    }

    public void sendQuietMessage(CommandSender sender, String[] args) {
        // player check
        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        if (player.hasPermission("rp.whisper") || player.hasPermission("rp.default")) {

            if (args.length > 0) {
                String message = ChatColor.RED + "" + String.format("%s: \"%s\"", main.utilities.getCard(player.getUniqueId()).getName(), main.utilities.createStringFromArgs(args));

                sendMessageToPlayersWithinDistance(player, message, 2);
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
