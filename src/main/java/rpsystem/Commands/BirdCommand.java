package rpsystem.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;
import static rpsystem.UtilityFunctions.createStringFromFirstArgOnwards;

public class BirdCommand {
    public static void sendBird(CommandSender sender, String[] args) {
        // player check
        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        // zero args check
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /bird (player-name) (message)");
            return;
        }

        Player targetPlayer = getServer().getPlayer(args[0]);

        if (targetPlayer == null) {
            player.sendMessage(ChatColor.RED + "That player isn't online!");
            return;
        }

        String message = createStringFromFirstArgOnwards(args);
        targetPlayer.sendMessage(ChatColor.BLUE + "A bird lands nearby and drops a message at your feet! It was sent by " + player.getName());
        targetPlayer.sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC + "'" + message + "'");



    }
}
