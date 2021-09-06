package dansplugins.rpsystem.eventhandlers;

import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.objects.CharacterCard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinHandler implements Listener {

    @EventHandler()
    public void handle(PlayerJoinEvent event) {
        if (!PersistentData.getInstance().hasCard(event.getPlayer().getUniqueId())) {
            CharacterCard newCard = new CharacterCard(event.getPlayer().getUniqueId());
            PersistentData.getInstance().getCards().add(newCard);
        }
    }

}
