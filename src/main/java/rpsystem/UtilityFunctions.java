package rpsystem;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class UtilityFunctions {
    public static String createStringFromFirstArgOnwards(String[] args, int startingArg) {
        StringBuilder name = new StringBuilder();
        for (int i = startingArg; i < args.length; i++) {
            name.append(args[i]);
            if (!(i == args.length - 1)) {
                name.append(" ");
            }
        }
        return name.toString();
    }

    public static void sendMessageToPlayersWithinDistance(Player player, String message, int distance) {
        Location playerLocation = player.getLocation();

        // for every online player
        for (Player potentialPlayer : getServer().getOnlinePlayers()) {

            // if in world
            if (potentialPlayer.getLocation().getWorld().getName() == playerLocation.getWorld().getName()) {

                // if within 30 blocks
                if (potentialPlayer.getLocation().distance(playerLocation) < 30) {
                    potentialPlayer.sendMessage(message);
                }
            }
        }
    }

}
