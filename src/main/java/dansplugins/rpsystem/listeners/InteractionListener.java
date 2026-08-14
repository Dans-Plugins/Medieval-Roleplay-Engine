package dansplugins.rpsystem.listeners;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.cards.CharacterCard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class InteractionListener implements Listener {
    private static final int RIGHT_CLICK_COOLDOWN_TICKS = 40;

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

            // rp.card.lookup is the node registered in plugin.yml for viewing another player's card;
            // the previously checked nodes are retained so that servers which already granted them keep working.
            if (!(player.hasPermission("rp.card.lookup") || player.hasPermission("rp.card.show.others")
                    || player.hasPermission("rp.card.*") || player.hasPermission("rp.default"))) {
                return;
            }

            // the cooldown entry is only added once the interaction is actually served, so that a rejected
            // player is never left on the cooldown for the remainder of the session
            if (!medievalRoleplayEngine.ephemeralData.getPlayersWithRightClickCooldown().contains(player.getUniqueId())) {
                medievalRoleplayEngine.ephemeralData.getPlayersWithRightClickCooldown().add(player.getUniqueId());

                medievalRoleplayEngine.messenger.sendCardInfoToPlayer(card, player);

                medievalRoleplayEngine.getServer().getScheduler().runTaskLater(medievalRoleplayEngine, new Runnable() {
                    @Override
                    public void run() {
                        medievalRoleplayEngine.ephemeralData.getPlayersWithRightClickCooldown().remove(player.getUniqueId());

                    }
                }, RIGHT_CLICK_COOLDOWN_TICKS);

            }

        }
    }

}
