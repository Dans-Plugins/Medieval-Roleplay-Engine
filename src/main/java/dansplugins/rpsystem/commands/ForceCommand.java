package dansplugins.rpsystem.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 * @brief This command will allow operators to force certain actions to occur.
 */
public class ForceCommand extends AbstractPluginCommand {

    public ForceCommand() {
        super(new ArrayList<>(Arrays.asList("force")), new ArrayList<>(Arrays.asList("rp.force")));
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        // TODO: implement
        commandSender.sendMessage(ChatColor.RED + "This command isn't implemented yet.");
        return false;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        // TODO: implement
        commandSender.sendMessage(ChatColor.RED + "This command isn't implemented yet.");
        return false;
    }
}