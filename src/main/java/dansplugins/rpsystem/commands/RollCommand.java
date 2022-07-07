package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.utils.ColorChecker;
import dansplugins.rpsystem.utils.Messenger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class RollCommand extends AbstractPluginCommand {
    private final Messenger messenger;
    private final ColorChecker colorChecker;

    public RollCommand(Messenger messenger, ColorChecker colorChecker) {
        super(new ArrayList<>(Arrays.asList("roll")), new ArrayList<>(Arrays.asList("mre.roll")));
        this.messenger = messenger;
        this.colorChecker = colorChecker;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        // TODO: implement
        return false;
    }

    public boolean execute(CommandSender sender, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 0) {
                try {
                    int max = Integer.parseInt(args[0]);
                    messenger.sendRPMessageToPlayersWithinDistance(player, colorChecker.getNeutralAlertColor() + "" + ChatColor.ITALIC + player.getName() + " has rolled a " + execute(max) + " out of " + max + ".", 25);
                }
                catch(Exception ignored) {

                }
            }

        }
        return false;
    }

    private int execute(int max) {
        return (int)(Math.random() * max + 1);
    }
}