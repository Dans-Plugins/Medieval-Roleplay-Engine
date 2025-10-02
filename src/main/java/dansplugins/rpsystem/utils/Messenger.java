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

        if (medievalRoleplayEngine.configService.getBoolean("logChat")) {
            logMessageToConsole("RP", player.getDisplayName(), message);
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

        if (medievalRoleplayEngine.configService.getBoolean("logChat")) {
            logMessageToConsole("RP", player.getDisplayName(), message);
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

        if (medievalRoleplayEngine.configService.getBoolean("logChat")) {
            logMessageToConsole("OOC", player.getDisplayName(), message);
        }

        return numPlayersWhoHeard;
    }

    public void sendCardInfoToPlayer(CharacterCard card, Player player) {
        player.sendMessage(ChatColor.BOLD + "" + medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "\n----------\n" + "Character Card of " + Bukkit.getOfflinePlayer(card.getPlayerUUID()).getName() + "\n----------\n");
        player.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "Name: " + card.getName());
        
        if (medievalRoleplayEngine.configService.getBoolean("cardShowRace")) {
            String raceLabel = medievalRoleplayEngine.configService.getString("cardLabelRace");
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + raceLabel + ": " + card.getRace());
        }
        
        if (medievalRoleplayEngine.configService.getBoolean("cardShowSubculture")) {
            String subcultureLabel = medievalRoleplayEngine.configService.getString("cardLabelSubculture");
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + subcultureLabel + ": " + card.getSubculture());
        }
        
        if (medievalRoleplayEngine.configService.getBoolean("cardShowAge")) {
            String ageLabel = medievalRoleplayEngine.configService.getString("cardLabelAge");
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + ageLabel + ": " + card.getAge());
        }
        
        if (medievalRoleplayEngine.configService.getBoolean("cardShowGender")) {
            String genderLabel = medievalRoleplayEngine.configService.getString("cardLabelGender");
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + genderLabel + ": " + card.getGender());
        }
        
        if (medievalRoleplayEngine.configService.getBoolean("cardShowReligion")) {
            String religionLabel = medievalRoleplayEngine.configService.getString("cardLabelReligion");
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + religionLabel + ": " + card.getReligion());
        }
    }

    /**
     * Log a message to the console to allow moderators to see what is being said in chat
     * @param chat the type of chat (RP, OOC, etc.)
     * @param playerName the name of the player who sent the message
     * @param message the message that was sent
     */
    private void logMessageToConsole(String chat, String playerName, String message) {
        medievalRoleplayEngine.getLogger().info("[" + chat + "] " + playerName + ": " + message);
    }
}
