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

        if (main.playersWithBusyBirds.contains(player.getName())) {
            player.sendMessage(ChatColor.RED + "Your bird is already on a mission!");
            return;
        }

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

        String message = createStringFromFirstArgOnwards(args, 1);

        if (!(player.getLocation().getWorld().getName().equalsIgnoreCase(targetPlayer.getLocation().getWorld().getName()))) {
            player.sendMessage(ChatColor.RED + "You can't send a bird to a player in another world.");
            return;
        }

        double distance = player.getLocation().distance(targetPlayer.getLocation());
        int blocksPerSecond = 20;
        int seconds = (int)distance/blocksPerSecond;

        getServer().getScheduler().runTaskLater(main, new Runnable() {
            @Override
            public void run() {
                targetPlayer.sendMessage(ChatColor.GREEN + "A bird lands nearby and drops a message at your feet! It was sent by " + player.getName() + ". It reads:");
                targetPlayer.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "'" + message + "'");
                player.sendMessage(ChatColor.GREEN + "Your bird has reached " + targetPlayer.getName() + "!");
                main.playersWithBusyBirds.remove(player.getName());

            }
        }, seconds * 20);

        player.sendMessage(ChatColor.GREEN + "The bird flies off with your message.");
        main.playersWithBusyBirds.add(player.getName());
    }
}
