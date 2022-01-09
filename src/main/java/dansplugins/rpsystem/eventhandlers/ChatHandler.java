package dansplugins.rpsystem.eventhandlers;

import dansplugins.factionsystem.externalapi.MF_Faction;
import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.utils.Messenger;
import dansplugins.rpsystem.data.EphemeralData;
import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.integrators.MedievalFactionsIntegrator;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * @author Daniel McCoy Stephenson
 */
public class ChatHandler implements Listener {

    @EventHandler()
    public void handle(AsyncPlayerChatEvent event) {
        if (!MedievalRoleplayEngine.getInstance().getPonderAPI().getConfigService().getBoolean("chatFeaturesEnabled")) {
            return;
        }

        int localChatRadius = MedievalRoleplayEngine.getInstance().getConfig().getInt("localChatRadius");

        if (MedievalFactionsIntegrator.getInstance().isMedievalFactionsPresent()) {
            if (MedievalFactionsIntegrator.getInstance().getAPI().isPlayerInFactionChat(event.getPlayer())) {
                return;
            }
        }

        String localChatColorString = MedievalRoleplayEngine.getInstance().getConfig().getString("localChatColor");
        if (EphemeralData.getInstance().getPlayersSpeakingInLocalChat().contains(event.getPlayer().getUniqueId())) {
            // get color and character name
            ChatColor localChatColor = ColorChecker.getInstance().getColorByName(localChatColorString);
            String characterName = PersistentData.getInstance().getCharacter(event.getPlayer().getUniqueId()).getInfo("name");

            if (EphemeralData.getInstance().getPlayersWhoHaveHiddenLocalChat().contains(event.getPlayer().getUniqueId())) {
                event.getPlayer().sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "You have hidden local chat. Type '/rp show' to talk in local chat.");
                event.setCancelled(true);
                return;
            }

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

        if (!MedievalRoleplayEngine.getInstance().getPonderAPI().getConfigService().getBoolean("legacyChat")) {

            if (EphemeralData.getInstance().getPlayersWhoHaveHiddenGlobalChat().contains(event.getPlayer().getUniqueId())) {
                event.getPlayer().sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "You have hidden global chat. Type '/ooc show' to talk in global chat.");
                event.setCancelled(true);
                return;
            }

            // global chat
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("Attempting to send global message to " + onlinePlayer.getName()); }
                if (!EphemeralData.getInstance().getPlayersWhoHaveHiddenGlobalChat().contains(onlinePlayer.getUniqueId())) {
                    if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("Preparing message: '" + event.getMessage() + "'"); }

                    // we are good to send the message

                    if (MedievalFactionsIntegrator.getInstance().isMedievalFactionsPresent()
                            && MedievalFactionsIntegrator.getInstance().getAPI().isPrefixesFeatureEnabled()
                            && MedievalFactionsIntegrator.getInstance().getAPI().getFaction(event.getPlayer()) != null) {

                        if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("Preparing message with prefix from Medieval Factions"); }

                        MF_Faction playersFaction = MedievalFactionsIntegrator.getInstance().getAPI().getFaction(event.getPlayer());

                        // prefix format
                        String prefix = playersFaction.getPrefix();
                        String prefixColor = (String) playersFaction.getFlag("prefixColor");
                        event.setFormat(ColorChecker.getInstance().getColorByName(prefixColor) + "" + "[" + prefix + "]" + "" + ChatColor.WHITE + "" + " <%s> %s");

                        // send message
                        onlinePlayer.sendMessage(ColorChecker.getInstance().getColorByName(prefixColor) + "" + "[" + prefix + "] " + ChatColor.WHITE + "<" + event.getPlayer().getName() + "> " + ChatColor.WHITE + event.getMessage());

                    }
                    else {

                        if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("Preparing message with regular format"); }

                        // regular format
                        event.setFormat(ChatColor.WHITE + "" + " <%s> %s");

                        // send message
                        onlinePlayer.sendMessage(ChatColor.WHITE + "<" + event.getPlayer().getName() + "> " + event.getMessage());

                    }

                }
                else {
                    if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("Player has hidden global chat!"); }
                }
            }
            event.setCancelled(true);
        }

    }

    private String removeStringContainedBetweenAstericks(String string) {
        String toReturn = "";

        String stringToRemove = getStringContainedBetweenAstericks(string);

        if (stringToRemove != null) {
            toReturn = string.replace("*" + stringToRemove + "*", "");

            if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("String after removal: " + toReturn); }

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
                if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("First asterick index: " + i); }
                break;
            }
        }

        int secondAsterickIndex = -1;
        for (int i = firstAsterickIndex + 1; i < string.length(); i++) {
            if (string.charAt(i) == '*') {
                secondAsterickIndex = i;
                if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("Second asterick index: " + i); }
                break;
            }
        }

        if (firstAsterickIndex != -1 && secondAsterickIndex != -1) {
            if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("String contained between astericks: " + toReturn); }
            return string.substring(firstAsterickIndex + 1, secondAsterickIndex);
        }
        else {
            return null;
        }
    }
}