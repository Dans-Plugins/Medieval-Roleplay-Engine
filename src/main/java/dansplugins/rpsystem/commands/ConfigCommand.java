package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.managers.ConfigManager;
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
        // TODO: implement
        return false;
    }

    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;

        if (!(player.hasPermission("rp.config") || player.hasPermission("rp.admin"))) {
            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "You don't have permission to configure Medieval Roleplay Engine!");
            return false;
        }

        if (args.length < 1) {
            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Valid subcommands: show, set");
            return false;
        }

        if (args[0].equalsIgnoreCase("show")) {
            // no further arguments needed, list config
            ConfigManager.getInstance().sendPlayerConfigList(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {

            // two more arguments needed
            if (args.length > 2) {

                String option = args[1];
                String value = args[2];

                ConfigManager.getInstance().setConfigOption(option, value, player);
                return true;
            }
            else {
                player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Usage: /rpconfig set (option) (value)");
                return false;
            }

        }

        player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Valid subcommands: show, set");

        return false;
    }

}
