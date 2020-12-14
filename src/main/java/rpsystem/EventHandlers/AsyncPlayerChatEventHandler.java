package rpsystem.EventHandlers;

import org.bukkit.ChatColor;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import rpsystem.MedievalRoleplayEngine;

import static rpsystem.Utilities.sendMessageToPlayersWithinDistance;

public class AsyncPlayerChatEventHandler {

    MedievalRoleplayEngine medievalRoleplayEngine = null;

    public AsyncPlayerChatEventHandler(MedievalRoleplayEngine plugin) {
        medievalRoleplayEngine = plugin;
    }

    public void handle(AsyncPlayerChatEvent event) {
        if (medievalRoleplayEngine.playersSpeakingInLocalChat.contains(event.getPlayer().getUniqueId())) {
            sendMessageToPlayersWithinDistance(event.getPlayer(), ChatColor.GRAY + "" + String.format("%s: \"%s\"", medievalRoleplayEngine.utilities.getCard(event.getPlayer().getUniqueId()).getName(), event.getMessage()), 25);
            event.setCancelled(true);
        }
    }

}
