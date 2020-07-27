package rpsystem.EventHandlers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import rpsystem.CharacterCard;
import rpsystem.Main;

public class PlayerInteractAtEntityEventHandler {

    Main main = null;

    public PlayerInteractAtEntityEventHandler(Main plugin) {
        main = plugin;
    }

    public void handle(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Player) {

            Player target = (Player) event.getRightClicked();
            CharacterCard card = main.utilities.getCard(target.getName());

            Player player = event.getPlayer();

            if (!main.playersWithRightClickCooldown.contains(player.getName())) {
                main.playersWithRightClickCooldown.add(player.getName());

                if (player.hasPermission("rp.card.show.others") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.AQUA + "\n == " + "Character Card of " + card.getPlayerName() + " == ");
                    player.sendMessage(ChatColor.AQUA + "Name: " + card.getName());
                    player.sendMessage(ChatColor.AQUA + "Race: " + card.getRace());
                    player.sendMessage(ChatColor.AQUA + "Subculture: " + card.getSubculture());
                    player.sendMessage(ChatColor.AQUA + "Age: " + card.getAge());
                    player.sendMessage(ChatColor.AQUA + "Gender: " + card.getGender());
                    player.sendMessage(ChatColor.AQUA + "Religion: " + card.getReligion());

                    int seconds = 2;
                    main.getServer().getScheduler().runTaskLater(main, new Runnable() {
                        @Override
                        public void run() {
                            main.playersWithRightClickCooldown.remove(player.getName());

                        }
                    }, seconds * 20);
                }

            }

        }
    }

}
