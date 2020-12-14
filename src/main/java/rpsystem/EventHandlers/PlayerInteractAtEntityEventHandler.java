package rpsystem.EventHandlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import rpsystem.Objects.CharacterCard;
import rpsystem.MedievalRoleplayEngine;

import static org.bukkit.Bukkit.getPlayer;

public class PlayerInteractAtEntityEventHandler {

    MedievalRoleplayEngine medievalRoleplayEngine = null;

    public PlayerInteractAtEntityEventHandler(MedievalRoleplayEngine plugin) {
        medievalRoleplayEngine = plugin;
    }

    public void handle(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Player) {

            Player target = (Player) event.getRightClicked();
            CharacterCard card = medievalRoleplayEngine.utilities.getCard(target.getUniqueId());

            Player player = event.getPlayer();

            if (!medievalRoleplayEngine.playersWithRightClickCooldown.contains(player.getUniqueId())) {
                medievalRoleplayEngine.playersWithRightClickCooldown.add(player.getUniqueId());

                if (player.hasPermission("rp.card.show.others") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.AQUA + "\n == " + "Character Card of " + Bukkit.getOfflinePlayer(card.getPlayerUUID()).getName() + " == ");
                    player.sendMessage(ChatColor.AQUA + "Name: " + card.getName());
                    player.sendMessage(ChatColor.AQUA + "Race: " + card.getRace());
                    player.sendMessage(ChatColor.AQUA + "Subculture: " + card.getSubculture());
                    player.sendMessage(ChatColor.AQUA + "Age: " + card.getAge());
                    player.sendMessage(ChatColor.AQUA + "Gender: " + card.getGender());
                    player.sendMessage(ChatColor.AQUA + "Religion: " + card.getReligion());

                    int seconds = 2;
                    medievalRoleplayEngine.getServer().getScheduler().runTaskLater(medievalRoleplayEngine, new Runnable() {
                        @Override
                        public void run() {
                            medievalRoleplayEngine.playersWithRightClickCooldown.remove(player.getUniqueId());

                        }
                    }, seconds * 20);
                }

            }

        }
    }

}
