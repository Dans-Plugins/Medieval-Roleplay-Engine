package rpsystem.EventHandlers;

import org.bukkit.ChatColor;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import rpsystem.MedievalRoleplayEngine;
import rpsystem.Utilities;

import static rpsystem.Utilities.sendMessageToPlayersWithinDistance;

public class AsyncPlayerChatEventHandler {

    public void handle(AsyncPlayerChatEvent event) {
        if (MedievalRoleplayEngine.getInstance().playersSpeakingInLocalChat.contains(event.getPlayer().getUniqueId())) {
            sendMessageToPlayersWithinDistance(event.getPlayer(), ChatColor.GRAY + "" + String.format("%s: \"%s\"", Utilities.getInstance().getCard(event.getPlayer().getUniqueId()).getName(), event.getMessage()), 25);
            event.setCancelled(true);
        }
    }

}
