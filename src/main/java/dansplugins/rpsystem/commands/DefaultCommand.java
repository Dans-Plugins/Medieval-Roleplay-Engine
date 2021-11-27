package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

public class DefaultCommand extends AbstractCommand {
    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("default"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("rp.default"));

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
        commandSender.sendMessage(ChatColor.AQUA + "MedievalRoleplayEngine " + MedievalRoleplayEngine.getInstance().getVersion());
        commandSender.sendMessage(ChatColor.AQUA + "Developed by: Daniel Stephenson");
        commandSender.sendMessage(ChatColor.AQUA + "Wiki: https://github.com/dmccoystephenson/MedievalRoleplayEngine/wiki");
        commandSender.sendMessage("---");
        commandSender.sendMessage(ChatColor.AQUA + "Tip: You can view a list of helpful commands by typing /rp help");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}
