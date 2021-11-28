package dansplugins.rpsystem.utils;

import dansplugins.rpsystem.data.EphemeralData;
import dansplugins.rpsystem.objects.RPCharacter;
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
                    if (!EphemeralData.getInstance().getPlayersWhoHaveHiddenLocalChat().contains(potentialPlayer.getUniqueId())) {
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
                        if (!EphemeralData.getInstance().getPlayersWhoHaveHiddenLocalChat().contains(potentialPlayer.getUniqueId())) {
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
                    if (!EphemeralData.getInstance().getPlayersWhoHaveHiddenLocalOOCChat().contains(potentialPlayer.getUniqueId())) {
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
        player.sendMessage(ChatColor.BOLD + "" + ColorChecker.getInstance().getNeutralAlertColor() + "\n----------\n" + "Character Card of " + Bukkit.getOfflinePlayer(card.getPlayerUUID()).getName() + "\n----------\n");
        player.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "Name: " + card.getName());
        player.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "Race: " + card.getRace());
        player.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "Subculture: " + card.getSubculture());
        player.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "Age: " + card.getAge());
        player.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "Gender: " + card.getGender());
        player.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "Religion: " + card.getReligion());
        if (MedievalFactionsIntegrator.getInstance().isMedievalFactionsPresent()) {
            MF_Faction faction = MedievalFactionsIntegrator.getInstance().getAPI().getFaction(card.getPlayerUUID());
            int power = MedievalFactionsIntegrator.getInstance().getAPI().getPower(card.getPlayerUUID());
            if (faction != null) {
                player.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "Faction: " + faction.getName());
            }
            else {
                player.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "Faction: N/A");
            }
            player.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "Power: " + power);
        }
        */
    }
}
