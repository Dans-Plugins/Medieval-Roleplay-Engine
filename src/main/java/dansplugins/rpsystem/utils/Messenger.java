package dansplugins.rpsystem.utils;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.cards.CharacterCard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Predicate;

import static org.bukkit.Bukkit.getServer;

public class Messenger {
    private final MedievalRoleplayEngine medievalRoleplayEngine;

    public Messenger(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    public int sendRPMessageToPlayersWithinDistance(Player sender, String message, int distance) {
        int recipientCount = deliverMessageToNearbyPlayers(sender, message, distance, false,
                uuid -> medievalRoleplayEngine.ephemeralData.getPlayersWhoHaveHiddenLocalChat().contains(uuid));
        logChatIfEnabled("RP", sender.getDisplayName(), message);
        return recipientCount;
    }

    public int sendRPMessageToPlayersWithinDistanceExcludingTarget(Player sender, String message, int distance) {
        int recipientCount = deliverMessageToNearbyPlayers(sender, message, distance, true,
                uuid -> medievalRoleplayEngine.ephemeralData.getPlayersWhoHaveHiddenLocalChat().contains(uuid));
        logChatIfEnabled("RP", sender.getDisplayName(), message);
        return recipientCount;
    }

    public int sendOOCMessageToPlayersWithinDistance(Player sender, String message, int distance) {
        int recipientCount = deliverMessageToNearbyPlayers(sender, message, distance, false,
                uuid -> medievalRoleplayEngine.ephemeralData.getPlayersWhoHaveHiddenLocalOOCChat().contains(uuid));
        logChatIfEnabled("OOC", sender.getDisplayName(), message);
        return recipientCount;
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

    private int deliverMessageToNearbyPlayers(Player sender, String message, int distance,
                                               boolean excludeSender, Predicate<UUID> hiddenChatCheck) {
        Location senderLocation = sender.getLocation();
        if (senderLocation.getWorld() == null) {
            return 0;
        }
        int recipientCount = 0;
        for (Player nearby : getServer().getOnlinePlayers()) {
            Location nearbyLocation = nearby.getLocation();
            if (nearbyLocation.getWorld() == null) {
                continue;
            }
            if (!nearbyLocation.getWorld().getName().equals(senderLocation.getWorld().getName())) {
                continue;
            }
            if (nearbyLocation.distance(senderLocation) >= distance) {
                continue;
            }
            if (excludeSender && nearby.getUniqueId().equals(sender.getUniqueId())) {
                continue;
            }
            if (hiddenChatCheck.test(nearby.getUniqueId())) {
                continue;
            }
            recipientCount++;
            nearby.sendMessage(message);
        }
        return recipientCount;
    }

    /**
     * Log a message to the console to allow moderators to see what is being said in chat.
     * @param chatType the type of chat (RP, OOC, etc.)
     * @param playerName the name of the player who sent the message
     * @param message the message that was sent
     */
    private void logChatIfEnabled(String chatType, String playerName, String message) {
        if (medievalRoleplayEngine.configService.getBoolean("logChat")) {
            medievalRoleplayEngine.getLogger().info("[" + chatType + "] " + playerName + ": " + message);
        }
    }
}
