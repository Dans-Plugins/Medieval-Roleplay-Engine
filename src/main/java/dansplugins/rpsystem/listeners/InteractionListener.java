package dansplugins.rpsystem.listeners;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.cards.CharacterCard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class InteractionListener implements Listener {
    private final MedievalRoleplayEngine medievalRoleplayEngine;

    public InteractionListener(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    @EventHandler()
    public void handle(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Player) {

            Player target = (Player) event.getRightClicked();
            CharacterCard card = medievalRoleplayEngine.cardRepository.getCard(target.getUniqueId());

            Player player = event.getPlayer();

            if (card == null) {
                return;
            }

            if (!medievalRoleplayEngine.getConfig().getBoolean("rightClickToViewCard")) {
                return;
            }

            if (!medievalRoleplayEngine.ephemeralData.getPlayersWithRightClickCooldown().contains(player.getUniqueId())) {
                medievalRoleplayEngine.ephemeralData.getPlayersWithRightClickCooldown().add(player.getUniqueId());

                if (player.hasPermission("rp.card.show.others") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                    medievalRoleplayEngine.messenger.sendCardInfoToPlayer(card, player);

                    int seconds = 2;
                    medievalRoleplayEngine.getServer().getScheduler().runTaskLater(medievalRoleplayEngine, new Runnable() {
                        @Override
                        public void run() {
                            medievalRoleplayEngine.ephemeralData.getPlayersWithRightClickCooldown().remove(player.getUniqueId());

                        }
                    }, seconds * 20);
                }

            }

        }
    }

}
