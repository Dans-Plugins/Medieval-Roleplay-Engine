package rpsystem.EventHandlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import rpsystem.MedievalRoleplayEngine;
import rpsystem.Objects.CharacterCard;
import rpsystem.Utilities;
import rpsystem.data.PersistentData;

public class PlayerJoinEventHandler implements Listener {

    @EventHandler()
    public void handle(PlayerJoinEvent event) {
        if (!Utilities.getInstance().hasCard(event.getPlayer().getUniqueId())) {
            CharacterCard newCard = new CharacterCard(event.getPlayer().getUniqueId());
            PersistentData.getInstance().getCards().add(newCard);
        }
    }

}
