package dansplugins.rpsystem.Commands;

import dansplugins.rpsystem.ColorChecker;
import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WhisperCommand {

    public void sendQuietMessage(CommandSender sender, String[] args) {

        int whisperChatRadius = MedievalRoleplayEngine.getInstance().getConfig().getInt("whisperChatRadius");
        String whisperChatColor =MedievalRoleplayEngine.getInstance().getConfig().getString("whisperChatColor");

        // player check
        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        if (player.hasPermission("rp.whisper") || player.hasPermission("rp.default")) {

            if (args.length > 0) {
                String message = ColorChecker.getInstance().getColorByName(whisperChatColor) + "" + String.format("%s whispers: \"%s\"", Utilities.getInstance().getCard(player.getUniqueId()).getName(), Utilities.getInstance().createStringFromArgs(args));

                int numPlayersWhoHeard = Utilities.getInstance().sendMessageToPlayersWithinDistance(player, message, whisperChatRadius);

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
