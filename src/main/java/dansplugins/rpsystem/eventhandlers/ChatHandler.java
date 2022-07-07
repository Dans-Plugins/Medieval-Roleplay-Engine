package dansplugins.rpsystem.eventhandlers;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.data.EphemeralData;
import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.services.ConfigService;
import dansplugins.rpsystem.utils.ColorChecker;
import dansplugins.rpsystem.utils.Messenger;
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
    private final ConfigService configService;
    private final MedievalRoleplayEngine medievalRoleplayEngine;
    private final EphemeralData ephemeralData;
    private final ColorChecker colorChecker;
    private final PersistentData persistentData;
    private final Messenger messenger;

    public ChatHandler(ConfigService configService, MedievalRoleplayEngine medievalRoleplayEngine, EphemeralData ephemeralData, ColorChecker colorChecker, PersistentData persistentData, Messenger messenger) {
        this.configService = configService;
        this.medievalRoleplayEngine = medievalRoleplayEngine;
        this.ephemeralData = ephemeralData;
        this.colorChecker = colorChecker;
        this.persistentData = persistentData;
        this.messenger = messenger;
    }

    @EventHandler()
    public void handle(AsyncPlayerChatEvent event) {
        if (!configService.getBoolean("chatFeaturesEnabled")) {
            return;
        }

        int localChatRadius = medievalRoleplayEngine.getConfig().getInt("localChatRadius");

        String localChatColorString = medievalRoleplayEngine.getConfig().getString("localChatColor");
        if (ephemeralData.getPlayersSpeakingInLocalChat().contains(event.getPlayer().getUniqueId())) {
            // get color and character name
            ChatColor localChatColor = colorChecker.getColorByName(localChatColorString);
            String characterName = persistentData.getCharacter(event.getPlayer().getUniqueId()).getInfo("name");

            if (ephemeralData.getPlayersWhoHaveHiddenLocalChat().contains(event.getPlayer().getUniqueId())) {
                event.getPlayer().sendMessage(colorChecker.getNegativeAlertColor() + "You have hidden local chat. Type '/rp show' to talk in local chat.");
                event.setCancelled(true);
                return;
            }

            // prepare message to send
            String messageToSend;
            if (!event.getMessage().contains("*")) {
                messageToSend = localChatColor + "" + String.format("%s: \"%s\"", characterName, event.getMessage());
                messenger.sendRPMessageToPlayersWithinDistance(event.getPlayer(), messageToSend, localChatRadius);
            }
            else {
                String messageWithoutEmote = removeStringContainedBetweenAstericks(event.getMessage());

                String emoteMessage = getStringContainedBetweenAstericks(event.getMessage());
                int emoteRadius = medievalRoleplayEngine.getConfig().getInt("emoteRadius");
                String emoteColorString = medievalRoleplayEngine.getConfig().getString("emoteColor");

                messageWithoutEmote = messageWithoutEmote.trim();

                messageToSend = localChatColor + "" + String.format("%s: \"%s\"", characterName, messageWithoutEmote);

                if (!messageWithoutEmote.equals("")) {
                    messenger.sendRPMessageToPlayersWithinDistance(event.getPlayer(), messageToSend, localChatRadius);
                }
                if (emoteMessage != null) {
                    messenger.sendRPMessageToPlayersWithinDistance(event.getPlayer(), colorChecker.getColorByName(emoteColorString) + "" + ChatColor.ITALIC + characterName + " " + emoteMessage, emoteRadius);
                }
            }

            event.setCancelled(true);
            return;
        }

        if (!configService.getBoolean("legacyChat")) {

            if (ephemeralData.getPlayersWhoHaveHiddenGlobalChat().contains(event.getPlayer().getUniqueId())) {
                event.getPlayer().sendMessage(colorChecker.getNegativeAlertColor() + "You have hidden global chat. Type '/ooc show' to talk in global chat.");
                event.setCancelled(true);
                return;
            }

            // global chat
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (medievalRoleplayEngine.isDebugEnabled()) { System.out.println("Attempting to send global message to " + onlinePlayer.getName()); }
                if (!ephemeralData.getPlayersWhoHaveHiddenGlobalChat().contains(onlinePlayer.getUniqueId())) {
                    if (medievalRoleplayEngine.isDebugEnabled()) { System.out.println("Preparing message: '" + event.getMessage() + "'"); }

                    // we are good to send the message
                    if (medievalRoleplayEngine.isDebugEnabled()) { System.out.println("Preparing message with regular format"); }

                    // regular format
                    event.setFormat(ChatColor.WHITE + "" + " <%s> %s");

                    // send message
                    onlinePlayer.sendMessage(ChatColor.WHITE + "<" + event.getPlayer().getName() + "> " + event.getMessage());

                }
                else {
                    if (medievalRoleplayEngine.isDebugEnabled()) { System.out.println("Player has hidden global chat!"); }
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