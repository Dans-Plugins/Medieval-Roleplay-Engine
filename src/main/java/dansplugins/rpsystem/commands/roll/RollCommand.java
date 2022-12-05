package dansplugins.rpsystem.commands.roll;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RollCommand {
    private final MedievalRoleplayEngine medievalRoleplayEngine;

    public RollCommand(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    public boolean rollDice(CommandSender sender, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("rp.roll") || player.hasPermission("rp.dice") || player.hasPermission("rp.default")) {
                if (args.length > 0) {
                    try {
                        int max = Integer.parseInt(args[0]);
                        medievalRoleplayEngine.messenger.sendRPMessageToPlayersWithinDistance(player, medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "" + ChatColor.ITALIC + player.getName() + " has rolled a " + rollDice(max) + " out of " + max + ".", 25);
                    }
                    catch(Exception ignored) {

                    }
                }
            }
            else {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need one the following permissions: 'rp.roll', 'rp.dice'");
            }

        }
        return false;
    }

    private int rollDice(int max) {
        return (int)(Math.random() * max + 1);
    }

}
