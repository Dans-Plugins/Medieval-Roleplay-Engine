package dansplugins.rpsystem.listeners;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.cards.CharacterCard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    private final MedievalRoleplayEngine medievalRoleplayEngine;

    public JoinListener(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    @EventHandler()
    public void handle(PlayerJoinEvent event) {
        if (!medievalRoleplayEngine.cardRepository.hasCard(event.getPlayer().getUniqueId())) {
            CharacterCard newCard = new CharacterCard(medievalRoleplayEngine, event.getPlayer().getUniqueId());
            medievalRoleplayEngine.cardRepository.getCards().add(newCard);
        }
    }

}
