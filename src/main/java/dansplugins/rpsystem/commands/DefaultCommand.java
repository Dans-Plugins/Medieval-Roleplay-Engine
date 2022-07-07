package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class DefaultCommand extends AbstractPluginCommand {
    private final MedievalRoleplayEngine medievalRoleplayEngine;

    public DefaultCommand(MedievalRoleplayEngine medievalRoleplayEngine) {
        super(new ArrayList<>(Arrays.asList("default")), new ArrayList<>(Arrays.asList("mre.default")));
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.AQUA + "MedievalRoleplayEngine " + medievalRoleplayEngine.getVersion());
        commandSender.sendMessage(ChatColor.AQUA + "Developed by: Daniel Stephenson");
        commandSender.sendMessage(ChatColor.AQUA + "Wiki: https://github.com/dmccoystephenson/MedievalRoleplayEngine/wiki");
        commandSender.sendMessage("---");
        commandSender.sendMessage(ChatColor.AQUA + "Tip: You can view a list of helpful commands by typing /mre help");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}