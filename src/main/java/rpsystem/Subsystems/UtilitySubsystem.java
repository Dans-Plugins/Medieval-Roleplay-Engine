package rpsystem.Subsystems;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import rpsystem.Objects.CharacterCard;
import rpsystem.Main;

import java.util.UUID;

import static org.bukkit.Bukkit.*;

public class UtilitySubsystem {

    Main main = null;

    public UtilitySubsystem(Main plugin) {
        main = plugin;
    }

    public boolean hasCard(UUID uuid) {
        for (CharacterCard card : main.cards) {
            if (card.getPlayerUUID().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public CharacterCard getCard(UUID uuid) {
        for (CharacterCard card : main.cards) {
            if (card.getPlayerUUID().equals(uuid)) {
                return card;
            }
        }
        return null;
    }

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
                if (potentialPlayer.getLocation().distance(playerLocation) < distance) {
                    potentialPlayer.sendMessage(message);
                }
            }
        }
    }

    // Pasarus wrote this
    public static UUID findUUIDBasedOnPlayerName(String playerName){
        // Check online
        for (Player player : getOnlinePlayers()){
            if (player.getName().equals(playerName)){
                return player.getUniqueId();
            }
        }

        // Check offline
        for (OfflinePlayer player : getOfflinePlayers()){
            try {
                if (player.getName().equals(playerName)){
                    return player.getUniqueId();
                }
            } catch (NullPointerException e) {
                // Fail silently as quit possibly common.
            }

        }

        return null;
    }

    public String createStringFromArgs(String[] args) {
        String toReturn = args[0];
        for (int i = 1; i < args.length; i++) {
            toReturn = toReturn + " " + args[i];
        }
        return toReturn;
    }

}
