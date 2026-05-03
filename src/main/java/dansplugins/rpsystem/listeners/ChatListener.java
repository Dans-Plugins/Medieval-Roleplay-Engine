package dansplugins.rpsystem.listeners;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import org.bukkit.ChatColor;
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
            ChatColor localChatColor = medievalRoleplayEngine.colorChecker.getColorByName(localChatColorString);
            String characterName = medievalRoleplayEngine.cardRepository.getCard(event.getPlayer().getUniqueId()).getName();

            if (medievalRoleplayEngine.ephemeralData.getPlayersWhoHaveHiddenLocalChat().contains(event.getPlayer().getUniqueId())) {
                event.getPlayer().sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "You have hidden local chat. Type '/rp show' to talk in local chat.");
                event.setCancelled(true);
                return;
            }

            String messageToSend;
            if (!event.getMessage().contains("*")) {
                messageToSend = localChatColor + "" + String.format("%s: \"%s\"", characterName, event.getMessage());
                medievalRoleplayEngine.messenger.sendRPMessageToPlayersWithinDistance(event.getPlayer(), messageToSend, localChatRadius);
            }
            else {
                String messageWithoutEmote = removeStringContainedBetweenAsterisks(event.getMessage());

                String emoteMessage = getStringContainedBetweenAsterisks(event.getMessage());
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
        String stringToRemove = getStringContainedBetweenAsterisks(string);

        if (stringToRemove != null) {
            String result = string.replace("*" + stringToRemove + "*", "");
            if (medievalRoleplayEngine.isDebugEnabled()) { System.out.println("String after removal: " + result); }
            return result;
        }
        else {
            return string;
        }

    }

    private String getStringContainedBetweenAsterisks(String string) {
        int firstAsteriskIndex = -1;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '*') {
                firstAsteriskIndex = i;
                if (medievalRoleplayEngine.isDebugEnabled()) { System.out.println("First asterisk index: " + i); }
                break;
            }
        }

        int secondAsteriskIndex = -1;
        for (int i = firstAsteriskIndex + 1; i < string.length(); i++) {
            if (string.charAt(i) == '*') {
                secondAsteriskIndex = i;
                if (medievalRoleplayEngine.isDebugEnabled()) { System.out.println("Second asterisk index: " + i); }
                break;
            }
        }

        if (firstAsteriskIndex != -1 && secondAsteriskIndex != -1) {
            String contained = string.substring(firstAsteriskIndex + 1, secondAsteriskIndex);
            if (medievalRoleplayEngine.isDebugEnabled()) { System.out.println("String contained between asterisks: " + contained); }
            return contained;
        }
        else {
            return null;
        }
    }

}
