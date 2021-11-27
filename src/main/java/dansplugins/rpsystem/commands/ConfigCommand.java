package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

public class ConfigCommand extends AbstractCommand {
    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("config"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("rp.config"));

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
        commandSender.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Usage: /rp config set (option) (value)");
        return false;
    }

    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Valid subcommands: show, set");
            return false;
        }

        if (args[0].equalsIgnoreCase("show")) {
            MedievalRoleplayEngine.getInstance().getPonderAPI().getConfigService().sendConfigList(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {

            if (args.length > 2) {

                String option = args[1];
                String value = args[2];

                MedievalRoleplayEngine.getInstance().getPonderAPI().getConfigService().setConfigOption(option, value, player);
                return true;
            }
            else {
                return execute(sender);
            }

        }

        player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Valid subcommands: show, set");

        return false;
    }

}
