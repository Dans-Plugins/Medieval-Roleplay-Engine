package dansplugins.rpsystem.utils;

import static org.bukkit.Bukkit.getServer;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import dansplugins.rpsystem.data.EphemeralData;
import dansplugins.rpsystem.objects.RPCharacter;

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

    @Deprecated
    public void sendCardInfoToPlayer(RPCharacter card, Player player) {
        /*
        player.sendMessage(ChatColor.BOLD + "" + colorChecker.getNeutralAlertColor() + "\n----------\n" + "Character Card of " + Bukkit.getOfflinePlayer(card.getPlayerUUID()).getName() + "\n----------\n");
        player.sendMessage(colorChecker.getNeutralAlertColor() + "Name: " + card.getName());
        player.sendMessage(colorChecker.getNeutralAlertColor() + "Race: " + card.getRace());
        player.sendMessage(colorChecker.getNeutralAlertColor() + "Subculture: " + card.getSubculture());
        player.sendMessage(colorChecker.getNeutralAlertColor() + "Age: " + card.getAge());
        player.sendMessage(colorChecker.getNeutralAlertColor() + "Gender: " + card.getGender());
        player.sendMessage(colorChecker.getNeutralAlertColor() + "Religion: " + card.getReligion());
        if (medievalFactionsIntegrator.isMedievalFactionsPresent()) {
            MF_Faction faction = medievalFactionsIntegrator.getAPI().getFaction(card.getPlayerUUID());
            int power = medievalFactionsIntegrator.getAPI().getPower(card.getPlayerUUID());
            if (faction != null) {
                player.sendMessage(colorChecker.getNeutralAlertColor() + "Faction: " + faction.getName());
            }
            else {
                player.sendMessage(colorChecker.getNeutralAlertColor() + "Faction: N/A");
            }
            player.sendMessage(colorChecker.getNeutralAlertColor() + "Power: " + power);
        }
        */
    }
}