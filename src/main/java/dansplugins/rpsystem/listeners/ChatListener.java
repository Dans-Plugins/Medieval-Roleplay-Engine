package dansplugins.rpsystem.listeners;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    private final MedievalRoleplayEngine medievalRoleplayEngine;

    public ChatListener(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    @EventHandler()
    public void handle(AsyncPlayerChatEvent event) {
        if (!medievalRoleplayEngine.configService.getBoolean("chatFeaturesEnabled")) {
            return;
        }

        int localChatRadius = medievalRoleplayEngine.getConfig().getInt("localChatRadius");

        String localChatColorString = medievalRoleplayEngine.getConfig().getString("localChatColor");
        if (medievalRoleplayEngine.ephemeralData.getPlayersSpeakingInLocalChat().contains(event.getPlayer().getUniqueId())) {
            // get color and character name
            ChatColor localChatColor = medievalRoleplayEngine.colorChecker.getColorByName(localChatColorString);
            String characterName = medievalRoleplayEngine.cardRepository.getCard(event.getPlayer().getUniqueId()).getName();

            if (medievalRoleplayEngine.ephemeralData.getPlayersWhoHaveHiddenLocalChat().contains(event.getPlayer().getUniqueId())) {
                event.getPlayer().sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "You have hidden local chat. Type '/rp show' to talk in local chat.");
                event.setCancelled(true);
                return;
            }

            // prepare message to send
            String messageToSend;
            if (!event.getMessage().contains("*")) {
                messageToSend = localChatColor + "" + String.format("%s: \"%s\"", characterName, event.getMessage());
                medievalRoleplayEngine.messenger.sendRPMessageToPlayersWithinDistance(event.getPlayer(), messageToSend, localChatRadius);
            }
            else {
                String messageWithoutEmote = removeStringContainedBetweenAsterisks(event.getMessage());

                String emoteMessage = getStringContainedBetweenAstericks(event.getMessage());
                int emoteRadius = medievalRoleplayEngine.getConfig().getInt("emoteRadius");
                String emoteColorString = medievalRoleplayEngine.getConfig().getString("emoteColor");

                messageWithoutEmote = messageWithoutEmote.trim();

                messageToSend = localChatColor + "" + String.format("%s: \"%s\"", characterName, messageWithoutEmote);

                if (!messageWithoutEmote.equals("")) {
                    medievalRoleplayEngine.messenger.sendRPMessageToPlayersWithinDistance(event.getPlayer(), messageToSend, localChatRadius);
                }
                if (emoteMessage != null) {
                    medievalRoleplayEngine.messenger.sendRPMessageToPlayersWithinDistance(event.getPlayer(), medievalRoleplayEngine.colorChecker.getColorByName(emoteColorString) + "" + ChatColor.ITALIC + characterName + " " + emoteMessage, emoteRadius);
                }
            }

            event.setCancelled(true);
        }
    }

    private String removeStringContainedBetweenAsterisks(String string) {
        String toReturn = "";

        String stringToRemove = getStringContainedBetweenAstericks(string);

        if (stringToRemove != null) {
            toReturn = string.replace("*" + stringToRemove + "*", "");

            if (medievalRoleplayEngine.isDebugEnabled()) { System.out.println("String after removal: " + toReturn); }

            return toReturn;
        }
        else {
            return string;
        }

    }

    private String getStringContainedBetweenAstericks(String string) {
        String toReturn = "";

        int firstAsterickIndex = -1;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '*') {
                firstAsterickIndex = i;
                if (medievalRoleplayEngine.isDebugEnabled()) { System.out.println("First asterick index: " + i); }
                break;
            }
        }

        int secondAsterickIndex = -1;
        for (int i = firstAsterickIndex + 1; i < string.length(); i++) {
            if (string.charAt(i) == '*') {
                secondAsterickIndex = i;
                if (medievalRoleplayEngine.isDebugEnabled()) { System.out.println("Second asterick index: " + i); }
                break;
            }
        }

        if (firstAsterickIndex != -1 && secondAsterickIndex != -1) {
            if (medievalRoleplayEngine.isDebugEnabled()) { System.out.println("String contained between astericks: " + toReturn); }
            return string.substring(firstAsterickIndex + 1, secondAsterickIndex);
        }
        else {
            return null;
        }
    }

}
