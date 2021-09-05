package dansplugins.rpsystem.eventhandlers;

import dansplugins.factionsystem.externalapi.MedievalFactionsAPI;
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

    private boolean debug = false;

    @EventHandler()
    public void handle(AsyncPlayerChatEvent event) {
        int localChatRadius = MedievalRoleplayEngine.getInstance().getConfig().getInt("localChatRadius");

        MedievalFactionsAPI mf_api = new MedievalFactionsAPI();
        if (mf_api != null) {
            if (mf_api.isPlayerInFactionChat(event.getPlayer())) {
                return;
            }
        }

        String localChatColorString = MedievalRoleplayEngine.getInstance().getConfig().getString("localChatColor");
        if (EphemeralData.getInstance().getPlayersSpeakingInLocalChat().contains(event.getPlayer().getUniqueId())) {
            // get color and character name
            ChatColor localChatColor = ColorChecker.getInstance().getColorByName(localChatColorString);
            String characterName = PersistentData.getInstance().getCard(event.getPlayer().getUniqueId()).getName();

            // prepare message to send
            String messageToSend;
            if (!event.getMessage().contains("*")) {
                messageToSend = localChatColor + "" + String.format("%s: \"%s\"", characterName, event.getMessage());
                Messenger.getInstance().sendRPMessageToPlayersWithinDistance(event.getPlayer(), messageToSend, localChatRadius);
            }
            else {
                String messageWithoutEmote = removeStringContainedBetweenAstericks(event.getMessage());

                String emoteMessage = getStringContainedBetweenAstericks(event.getMessage());
                int emoteRadius = MedievalRoleplayEngine.getInstance().getConfig().getInt("emoteRadius");
                String emoteColorString = MedievalRoleplayEngine.getInstance().getConfig().getString("emoteColor");

                messageWithoutEmote = messageWithoutEmote.trim();

                messageToSend = localChatColor + "" + String.format("%s: \"%s\"", characterName, messageWithoutEmote);

                if (!messageWithoutEmote.equals("")) {
                    Messenger.getInstance().sendRPMessageToPlayersWithinDistance(event.getPlayer(), messageToSend, localChatRadius);
                }
                if (emoteMessage != null) {
                    Messenger.getInstance().sendRPMessageToPlayersWithinDistance(event.getPlayer(), ColorChecker.getInstance().getColorByName(emoteColorString) + "" + ChatColor.ITALIC + characterName + " " + emoteMessage, emoteRadius);
                }
            }

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

    private String removeStringContainedBetweenAstericks(String string) {
        String toReturn = "";

        String stringToRemove = getStringContainedBetweenAstericks(string);

        if (stringToRemove != null) {
            toReturn = string.replace("*" + stringToRemove + "*", "");

            if (debug) { System.out.println("String after removal: " + toReturn); }

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
                if (debug) { System.out.println("First asterick index: " + i); }
                break;
            }
        }

        int secondAsterickIndex = -1;
        for (int i = firstAsterickIndex + 1; i < string.length(); i++) {
            if (string.charAt(i) == '*') {
                secondAsterickIndex = i;
                if (debug) { System.out.println("Second asterick index: " + i); }
                break;
            }
        }

        if (firstAsterickIndex != -1 && secondAsterickIndex != -1) {
            if (debug) { System.out.println("String contained between astericks: " + toReturn); }
            return string.substring(firstAsterickIndex + 1, secondAsterickIndex);
        }
        else {
            return null;
        }
    }

}
