package dansplugins.rpsystem;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class Messenger {

    private static Messenger instance;

    public static Messenger getInstance() {
        if (instance == null) {
            instance = new Messenger();
        }
        return instance;
    }

    public int sendMessageToPlayersWithinDistance(Player player, String message, int distance) {
        Location playerLocation = player.getLocation();

        int numPlayersWhoHeard = 0;

        // for every online player
        for (Player potentialPlayer : getServer().getOnlinePlayers()) {

            // if in world
            if (potentialPlayer.getLocation().getWorld().getName() == playerLocation.getWorld().getName()) {

                // if within 30 blocks
                if (potentialPlayer.getLocation().distance(playerLocation) < distance) {
                    numPlayersWhoHeard++;
                    potentialPlayer.sendMessage(message);
                }
            }
        }
        return numPlayersWhoHeard;
    }

    public int sendMessageToPlayersWithinDistanceExcludingTarget(Player player, String message, int distance) {
        Location playerLocation = player.getLocation();

        int numPlayersWhoHeard = 0;

        // for every online player
        for (Player potentialPlayer : getServer().getOnlinePlayers()) {

            // if in world
            if (potentialPlayer.getLocation().getWorld().getName() == playerLocation.getWorld().getName()) {

                // if within 30 blocks
                if (potentialPlayer.getLocation().distance(playerLocation) < distance) {

                    if (!potentialPlayer.getName().equalsIgnoreCase(player.getName())) {
                        numPlayersWhoHeard++;
                        potentialPlayer.sendMessage(message);
                    }

                }
            }
        }
        return numPlayersWhoHeard;
    }
}
