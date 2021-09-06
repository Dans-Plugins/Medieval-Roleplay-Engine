package dansplugins.rpsystem.eventhandlers;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.data.EphemeralData;
import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.objects.CharacterCard;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class InteractionHandler implements Listener {

    @EventHandler()
    public void handle(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Player) {

            Player target = (Player) event.getRightClicked();
            CharacterCard card = PersistentData.getInstance().getCard(target.getUniqueId());

            Player player = event.getPlayer();

            if (card == null) {
                return;
            }

            if (!MedievalRoleplayEngine.getInstance().getConfig().getBoolean("rightClickToViewCard")) {
                return;
            }

            if (!EphemeralData.getInstance().getPlayersWithRightClickCooldown().contains(player.getUniqueId())) {
                EphemeralData.getInstance().getPlayersWithRightClickCooldown().add(player.getUniqueId());

                if (player.hasPermission("rp.card.show.others") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                    player.sendMessage(ChatColor.BOLD + "" + ColorChecker.getInstance().getNeutralAlertColor() + "\n == " + "Character Card of " + target.getName() + " == ");
                    player.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "Name: " + card.getName());
                    player.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "Race: " + card.getRace());
                    player.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "Subculture: " + card.getSubculture());
                    player.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "Age: " + card.getAge());
                    player.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "Gender: " + card.getGender());
                    player.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "Religion: " + card.getReligion());

                    int seconds = 2;
                    MedievalRoleplayEngine.getInstance().getServer().getScheduler().runTaskLater(MedievalRoleplayEngine.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            EphemeralData.getInstance().getPlayersWithRightClickCooldown().remove(player.getUniqueId());

                        }
                    }, seconds * 20);
                }

            }

        }
    }

}
