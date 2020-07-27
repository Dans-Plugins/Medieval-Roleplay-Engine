package rpsystem.EventHandlers;

import org.bukkit.ChatColor;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import rpsystem.Main;

import static rpsystem.Subsystems.UtilitySubsystem.sendMessageToPlayersWithinDistance;

public class AsyncPlayerChatEventHandler {

    Main main = null;

    public AsyncPlayerChatEventHandler(Main plugin) {
        main = plugin;
    }

    public void handle(AsyncPlayerChatEvent event) {
        if (main.playersSpeakingInLocalChat.contains(event.getPlayer().getName())) {
            sendMessageToPlayersWithinDistance(event.getPlayer(), ChatColor.GRAY + "" + String.format("%s: \"%s\"", main.utilities.getCard(event.getPlayer().getName()).getName(), event.getMessage()), 25);
            event.setCancelled(true);
        }
    }

}
