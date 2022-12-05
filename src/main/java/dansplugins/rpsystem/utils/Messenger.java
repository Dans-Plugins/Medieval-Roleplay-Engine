package dansplugins.rpsystem.utils;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.cards.CharacterCard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Objects;

import static org.bukkit.Bukkit.getServer;

public class Messenger {
    private final MedievalRoleplayEngine medievalRoleplayEngine;

    public Messenger(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    public int sendRPMessageToPlayersWithinDistance(Player player, String message, int distance) {
        Location playerLocation = player.getLocation();

        int numPlayersWhoHeard = 0;

        // for every online player
        for (Player potentialPlayer : getServer().getOnlinePlayers()) {

            // if in world
            if (Objects.equals(potentialPlayer.getLocation().getWorld().getName(), playerLocation.getWorld().getName())) {

                // if within 30 blocks
                if (potentialPlayer.getLocation().distance(playerLocation) < distance) {

                    // if player has not hidden local chat
                    if (!medievalRoleplayEngine.ephemeralData.getPlayersWhoHaveHiddenLocalChat().contains(potentialPlayer.getUniqueId())) {
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
                        if (!medievalRoleplayEngine.ephemeralData.getPlayersWhoHaveHiddenLocalChat().contains(potentialPlayer.getUniqueId())) {
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
                    if (!medievalRoleplayEngine.ephemeralData.getPlayersWhoHaveHiddenLocalOOCChat().contains(potentialPlayer.getUniqueId())) {
                        numPlayersWhoHeard++;
                        potentialPlayer.sendMessage(message);
                    }

                }
            }
        }
        return numPlayersWhoHeard;
    }

    public void sendCardInfoToPlayer(CharacterCard card, Player player) {
        player.sendMessage(ChatColor.BOLD + "" + medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "\n----------\n" + "Character Card of " + Bukkit.getOfflinePlayer(card.getPlayerUUID()).getName() + "\n----------\n");
        player.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "Name: " + card.getName());
        player.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "Race: " + card.getRace());
        player.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "Subculture: " + card.getSubculture());
        player.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "Age: " + card.getAge());
        player.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "Gender: " + card.getGender());
        player.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "Religion: " + card.getReligion());
    }
}
