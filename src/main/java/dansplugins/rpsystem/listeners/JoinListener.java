package dansplugins.rpsystem.listeners;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.cards.CharacterCard;
import dansplugins.rpsystem.commands.rpnames.RPNamesCommand;
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
        
        // Enable RP names by default if configured
        if (medievalRoleplayEngine.configService.getBoolean("rpNamesEnabledByDefault")) {
            if (!medievalRoleplayEngine.ephemeralData.getPlayersWithRPNamesEnabled().contains(event.getPlayer().getUniqueId())) {
                medievalRoleplayEngine.ephemeralData.getPlayersWithRPNamesEnabled().add(event.getPlayer().getUniqueId());
            }
        }
        
        // Update player display name based on rpnames setting
        RPNamesCommand rpNamesCommand = new RPNamesCommand(medievalRoleplayEngine);
        rpNamesCommand.updatePlayerDisplayName(event.getPlayer());
    }

}
