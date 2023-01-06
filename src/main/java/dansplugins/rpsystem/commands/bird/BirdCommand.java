package dansplugins.rpsystem.commands.bird;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class BirdCommand {
    private final MedievalRoleplayEngine medievalRoleplayEngine;

    public BirdCommand(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    public void sendBird(CommandSender sender, String[] args) {
        // player check
        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        if (player.hasPermission("rp.bird") || player.hasPermission("rp.default")) {
            if (medievalRoleplayEngine.ephemeralData.getPlayersWithBusyBirds().contains(player.getUniqueId())) {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Your bird is already on a mission!");
                return;
            }

            // zero args check
            if (args.length < 2) {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Usage: /bird (player-name) (message)");
                return;
            }

            String message = medievalRoleplayEngine.argumentParser.createStringFromFirstArgOnwards(args, 1);

            Player targetPlayer = getServer().getPlayer(args[0]);

            if (targetPlayer == null) {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "That player isn't online!");
                return;
            }

            if (!(player.getLocation().getWorld().getName().equalsIgnoreCase(targetPlayer.getLocation().getWorld().getName()))) {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "You can't send a bird to a player in another world.");
                return;
            }

            double distance = player.getLocation().distance(targetPlayer.getLocation());
            int blocksPerSecond = medievalRoleplayEngine.configService.getInt("birdSpeed");
            int seconds = (int)distance/blocksPerSecond;

            getServer().getScheduler().runTaskLater(medievalRoleplayEngine, new Runnable() {
                @Override
                public void run() {
                    targetPlayer.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor() + "A bird lands nearby and drops a message at your feet! It was sent by " + player.getName() + ". It reads:\n");
                    targetPlayer.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor() + "" + ChatColor.ITALIC + "'" + message + "'");
                    player.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor() + "Your bird has reached " + targetPlayer.getName() + "!");
                    medievalRoleplayEngine.ephemeralData.getPlayersWithBusyBirds().remove(player.getUniqueId());
                    medievalRoleplayEngine.messenger.sendRPMessageToPlayersWithinDistanceExcludingTarget(targetPlayer, medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + String.format("A bird lands nearby and drops a message at the feet of %s!", targetPlayer.getName()), 10);
                }
            }, seconds * 20);

            player.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor() + "The bird flies off with your message.");
            medievalRoleplayEngine.ephemeralData.getPlayersWithBusyBirds().add(player.getUniqueId());
        }
        else {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.bird'");
        }

    }
}
