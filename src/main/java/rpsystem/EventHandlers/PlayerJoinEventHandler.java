package rpsystem.EventHandlers;

import org.bukkit.event.player.PlayerJoinEvent;
import rpsystem.Objects.CharacterCard;
import rpsystem.MedievalRoleplayEngine;

public class PlayerJoinEventHandler {

    MedievalRoleplayEngine medievalRoleplayEngine = null;

    public PlayerJoinEventHandler(MedievalRoleplayEngine plugin) {
        medievalRoleplayEngine = plugin;
    }

    public void handle(PlayerJoinEvent event) {
        if (!medievalRoleplayEngine.utilities.hasCard(event.getPlayer().getUniqueId())) {
            CharacterCard newCard = new CharacterCard(event.getPlayer().getUniqueId());
            medievalRoleplayEngine.cards.add(newCard);
        }
    }

}
