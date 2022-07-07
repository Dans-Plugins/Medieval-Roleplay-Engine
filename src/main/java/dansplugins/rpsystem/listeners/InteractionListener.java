package dansplugins.rpsystem.listeners;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.data.EphemeralData;
import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.objects.RPCharacter;
import dansplugins.rpsystem.services.ConfigService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

/**
 * @author Daniel McCoy Stephenson
 */
public class InteractionListener implements Listener {
    private final PersistentData persistentData;
    private final MedievalRoleplayEngine medievalRoleplayEngine;
    private final EphemeralData ephemeralData;
    private final ConfigService configService;

    public InteractionListener(PersistentData persistentData, MedievalRoleplayEngine medievalRoleplayEngine, EphemeralData ephemeralData, ConfigService configService) {
        this.persistentData = persistentData;
        this.medievalRoleplayEngine = medievalRoleplayEngine;
        this.ephemeralData = ephemeralData;
        this.configService = configService;
    }

    @EventHandler()
    public void handle(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Player) {

            Player target = (Player) event.getRightClicked();
            RPCharacter card = persistentData.getCharacter(target.getUniqueId());

            Player player = event.getPlayer();

            if (card == null) {
                return;
            }

            if (!configService.getBoolean("rightClickToViewCard")) {
                return;
            }

            if (!ephemeralData.getPlayersWithRightClickCooldown().contains(player.getUniqueId())) {
                ephemeralData.getPlayersWithRightClickCooldown().add(player.getUniqueId());

                if (player.hasPermission("rp.card.show.others") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default") || !player.hasMetadata("NPC")) {
                    card.sendCharacterInfo(player);

                    int seconds = 2;
                    medievalRoleplayEngine.getServer().getScheduler().runTaskLater(medievalRoleplayEngine, new Runnable() {
                        @Override
                        public void run() {
                            ephemeralData.getPlayersWithRightClickCooldown().remove(player.getUniqueId());

                        }
                    }, seconds * 20);
                }
            }
        }
    }
}