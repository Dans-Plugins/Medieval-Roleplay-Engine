package dansplugins.rpsystem.EventHandlers;

import dansplugins.rpsystem.Objects.CharacterCard;
import dansplugins.rpsystem.Utilities;
import dansplugins.rpsystem.data.PersistentData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinEventHandler implements Listener {

    @EventHandler()
    public void handle(PlayerJoinEvent event) {
        if (!Utilities.getInstance().hasCard(event.getPlayer().getUniqueId())) {
            CharacterCard newCard = new CharacterCard(event.getPlayer().getUniqueId());
            PersistentData.getInstance().getCards().add(newCard);
        }
    }

}
