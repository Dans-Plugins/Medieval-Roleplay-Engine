package dansplugins.rpsystem.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

public class UnsetCommand extends AbstractCommand {
    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("unset"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("rp.unset"));

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
        commandSender.sendMessage(ChatColor.RED + "This command isn't implemented yet.");
        return false;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        commandSender.sendMessage(ChatColor.RED + "This command isn't implemented yet.");
        return false;
    }
}