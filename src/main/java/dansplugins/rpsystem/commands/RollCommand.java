package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.utils.Messenger;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Daniel McCoy Stephenson
 */
public class RollCommand extends AbstractCommand {
    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("roll"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("rp.roll"));

    @Override
    public ArrayList<String> getNames() {
        return names;
    }

    @Override
    public ArrayList<String> getPermissions() {
        return permissions;
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
                    Messenger.getInstance().sendRPMessageToPlayersWithinDistance(player, ColorChecker.getInstance().getNeutralAlertColor() + "" + ChatColor.ITALIC + player.getName() + " has rolled a " + execute(max) + " out of " + max + ".", 25);
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