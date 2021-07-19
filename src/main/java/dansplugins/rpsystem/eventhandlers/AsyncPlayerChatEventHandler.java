package dansplugins.rpsystem.eventhandlers;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.Messenger;
import dansplugins.rpsystem.data.EphemeralData;
import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AsyncPlayerChatEventHandler implements Listener {

    @EventHandler()
    public void handle(AsyncPlayerChatEvent event) {
        int localChatRadius = MedievalRoleplayEngine.getInstance().getConfig().getInt("localChatRadius");
        String localChatColorString = MedievalRoleplayEngine.getInstance().getConfig().getString("localChatColor");
        if (EphemeralData.getInstance().getPlayersSpeakingInLocalChat().contains(event.getPlayer().getUniqueId())) {
            // get color and character name
            ChatColor localChatColor = ColorChecker.getInstance().getColorByName(localChatColorString);
            String characterName = PersistentData.getInstance().getCard(event.getPlayer().getUniqueId()).getName();

            // prepare message to send
            String messageToSend;
            if (!event.getMessage().contains("*")) {
                messageToSend = localChatColor + "" + String.format("%s: \"%s\"", characterName, event.getMessage());
            }
            else {
                String messageWithoutEmote = removeStringContainedBetweenAstericks(event.getMessage());

                String emoteMessage = getStringContainedBetweenAstericks(event.getMessage());
                String emoteColorString = MedievalRoleplayEngine.getInstance().getConfig().getString("emoteColor");
                ChatColor emoteColor = ColorChecker.getInstance().getColorByName(emoteColorString);

                messageToSend = localChatColor + "" + String.format("%s: \"%s\"", characterName, messageWithoutEmote) + emoteColor + "" + String.format("*%s*", emoteMessage);
            }

            Messenger.getInstance().sendRPMessageToPlayersWithinDistance(event.getPlayer(), messageToSend, localChatRadius);
            event.setCancelled(true);
            return;
        }

        // global chat
        /*
        ArrayList<UUID> playersWhoHaveLeftGlobalChat = EphemeralData.getInstance().getPlayersWhoHaveHiddenGlobalChat();
        if (playersWhoHaveLeftGlobalChat.size() != 0) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!playersWhoHaveLeftGlobalChat.contains(onlinePlayer.getUniqueId())) {
                    event.setFormat(event.getFormat());
                    onlinePlayer.sendMessage(event.getMessage()); // TODO: fix this
                }
            }
            event.setCancelled(true);
            return;
        }
        */

    }

    private String getStringContainedBetweenAstericks(String string) {
        String toReturn = "";

        int firstAsterickIndex = -1;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '*') {
                firstAsterickIndex = i;
                break;
            }
        }

        int secondAsterickIndex = -1;
        for (int i = firstAsterickIndex; i < string.length(); i++) {
            if (string.charAt(i) == '*') {
                secondAsterickIndex = i;
                break;
            }
        }

        toReturn = string.substring(firstAsterickIndex, secondAsterickIndex);

        return toReturn;
    }

    private String removeStringContainedBetweenAstericks(String string) {
        String toReturn = "";

        String stringToRemove = getStringContainedBetweenAstericks(string);

        toReturn = string.replace(stringToRemove, "");

        return toReturn;
    }

}
