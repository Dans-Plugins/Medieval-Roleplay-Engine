package dansplugins.rpsystem.commands.roll;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RollCommand {
    private static final int DEFAULT_DIE_SIZE = 20;

    private final MedievalRoleplayEngine medievalRoleplayEngine;

    public RollCommand(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    public boolean rollDice(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (!(player.hasPermission("rp.roll") || player.hasPermission("rp.dice") || player.hasPermission("rp.default"))) {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need one of the following permissions: 'rp.roll', 'rp.dice'");
            return false;
        }

        int max = DEFAULT_DIE_SIZE;
        if (args.length > 0) {
            try {
                max = Integer.parseInt(args[0]);
                if (max < 1) {
                    player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Please provide a positive number to roll.");
                    return false;
                }
            } catch (NumberFormatException e) {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "'" + args[0] + "' is not a valid number. Usage: /roll [max]");
                return false;
            }
        }

        medievalRoleplayEngine.messenger.sendRPMessageToPlayersWithinDistance(player, medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "" + ChatColor.ITALIC + player.getName() + " has rolled a " + rollDice(max) + " out of " + max + ".", 25);
        return true;
    }

    private int rollDice(int max) {
        return (int)(Math.random() * max + 1);
    }

}
