package rpsystem.EventHandlers;

import org.bukkit.event.player.PlayerJoinEvent;
import rpsystem.Objects.CharacterCard;
import rpsystem.Main;

public class PlayerJoinEventHandler {

    Main main = null;

    public PlayerJoinEventHandler(Main plugin) {
        main = plugin;
    }

    public void handle(PlayerJoinEvent event) {
        if (!main.utilities.hasCard(event.getPlayer().getUniqueId())) {
            CharacterCard newCard = new CharacterCard(event.getPlayer().getUniqueId());
            main.cards.add(newCard);
        }
    }

}
