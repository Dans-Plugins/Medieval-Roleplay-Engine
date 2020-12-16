package rpsystem.EventHandlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import rpsystem.MedievalRoleplayEngine;
import rpsystem.Objects.CharacterCard;
import rpsystem.Utilities;

public class PlayerJoinEventHandler implements Listener {

    @EventHandler()
    public void handle(PlayerJoinEvent event) {
        if (!Utilities.getInstance().hasCard(event.getPlayer().getUniqueId())) {
            CharacterCard newCard = new CharacterCard(event.getPlayer().getUniqueId());
            MedievalRoleplayEngine.getInstance().cards.add(newCard);
        }
    }

}
