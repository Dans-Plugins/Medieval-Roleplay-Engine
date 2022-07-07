package dansplugins.rpsystem.utils;

import dansplugins.rpsystem.data.EphemeralData;
import dansplugins.rpsystem.objects.RPCharacter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

/**
 * @author Daniel McCoy Stephenson
 */
public class Messenger {
    private final EphemeralData ephemeralData;

    public Messenger(EphemeralData ephemeralData) {
        this.ephemeralData = ephemeralData;
    }

    public int sendRPMessageToPlayersWithinDistance(Player player, String message, int distance) {
        Location playerLocation = player.getLocation();

        int numPlayersWhoHeard = 0;

        // for every online player
        for (Player potentialPlayer : getServer().getOnlinePlayers()) {

            // if in world
            if (potentialPlayer.getLocation().getWorld().getName() == playerLocation.getWorld().getName()) {

                // if within 30 blocks
                if (potentialPlayer.getLocation().distance(playerLocation) < distance) {

                    // if player has not hidden local chat
                    if (!ephemeralData.getPlayersWhoHaveHiddenLocalChat().contains(potentialPlayer.getUniqueId())) {
                        numPlayersWhoHeard++;
                        potentialPlayer.sendMessage(message);
                    }

                }
            }
        }
        return numPlayersWhoHeard;
    }

    public int sendRPMessageToPlayersWithinDistanceExcludingTarget(Player player, String message, int distance) {
        Location playerLocation = player.getLocation();

        int numPlayersWhoHeard = 0;

        // for every online player
        for (Player potentialPlayer : getServer().getOnlinePlayers()) {

            // if in world
            if (potentialPlayer.getLocation().getWorld().getName() == playerLocation.getWorld().getName()) {

                // if within 30 blocks
                if (potentialPlayer.getLocation().distance(playerLocation) < distance) {

                    if (!potentialPlayer.getName().equalsIgnoreCase(player.getName())) {

                        // if player has not hidden local chat
                        if (!ephemeralData.getPlayersWhoHaveHiddenLocalChat().contains(potentialPlayer.getUniqueId())) {
                            numPlayersWhoHeard++;
                            potentialPlayer.sendMessage(message);
                        }

                    }

                }
            }
        }
        return numPlayersWhoHeard;
    }

    public int sendOOCMessageToPlayersWithinDistance(Player player, String message, int distance) {
        Location playerLocation = player.getLocation();

        int numPlayersWhoHeard = 0;

        // for every online player
        for (Player potentialPlayer : getServer().getOnlinePlayers()) {

            // if in world
            if (potentialPlayer.getLocation().getWorld().getName() == playerLocation.getWorld().getName()) {

                // if within 30 blocks
                if (potentialPlayer.getLocation().distance(playerLocation) < distance) {

                    // if player has not hidden local OOC chat
                    if (!ephemeralData.getPlayersWhoHaveHiddenLocalOOCChat().contains(potentialPlayer.getUniqueId())) {
                        numPlayersWhoHeard++;
                        potentialPlayer.sendMessage(message);
                    }

                }
            }
        }
        return numPlayersWhoHeard;
    }
}