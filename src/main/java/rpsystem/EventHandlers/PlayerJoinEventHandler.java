package rpsystem.EventHandlers;

import org.bukkit.event.player.PlayerJoinEvent;
import rpsystem.MedievalRoleplayEngine;
import rpsystem.Objects.CharacterCard;

public class PlayerJoinEventHandler {

    public void handle(PlayerJoinEvent event) {
        if (!MedievalRoleplayEngine.getInstance().utilities.hasCard(event.getPlayer().getUniqueId())) {
            CharacterCard newCard = new CharacterCard(event.getPlayer().getUniqueId());
            MedievalRoleplayEngine.getInstance().cards.add(newCard);
        }
    }

}
