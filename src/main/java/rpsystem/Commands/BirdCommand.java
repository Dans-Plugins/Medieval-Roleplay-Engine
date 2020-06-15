package rpsystem.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import rpsystem.Main;

import static org.bukkit.Bukkit.getServer;
import static rpsystem.UtilityFunctions.createStringFromFirstArgOnwards;

public class BirdCommand {

    Main main = null;

    public BirdCommand(Main plugin) {
        main = plugin;
    }

    public void sendBird(CommandSender sender, String[] args) {
        // player check
        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        // zero args check
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /bird (player-name) (message)");
            return;
        }

        Player targetPlayer = getServer().getPlayer(args[0]);

        if (targetPlayer == null) {
            player.sendMessage(ChatColor.RED + "That player isn't online!");
            return;
        }

        String message = createStringFromFirstArgOnwards(args);

        int seconds = 5;

        getServer().getScheduler().runTaskLater(main, new Runnable() {
            @Override
            public void run() {
                targetPlayer.sendMessage(ChatColor.BLUE + "A bird lands nearby and drops a message at your feet! It was sent by " + player.getName() + ". It reads:");
                targetPlayer.sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC + "'" + message + "'");
                player.sendMessage("Your bird has reached " + targetPlayer.getName() + "!");
            }
        }, seconds * 20);

        player.sendMessage(ChatColor.GREEN + "The bird flies off with your message.");
    }
}
