package dansplugins.rpsystem.eventhandlers;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.Messenger;
import dansplugins.rpsystem.data.EphemeralData;
import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AsyncPlayerChatEventHandler implements Listener {

    @EventHandler()
    public void handle(AsyncPlayerChatEvent event) {
        int localChatRadius = MedievalRoleplayEngine.getInstance().getConfig().getInt("localChatRadius");
        String localChatColor = MedievalRoleplayEngine.getInstance().getConfig().getString("localChatColor");
        if (EphemeralData.getInstance().getPlayersSpeakingInLocalChat().contains(event.getPlayer().getUniqueId())) {
            Messenger.getInstance().sendMessageToPlayersWithinDistance(event.getPlayer(), ColorChecker.getInstance().getColorByName(localChatColor) + "" + String.format("%s: \"%s\"", PersistentData.getInstance().getCard(event.getPlayer().getUniqueId()).getName(), event.getMessage()), localChatRadius);
            event.setCancelled(true);
        }
    }

}
