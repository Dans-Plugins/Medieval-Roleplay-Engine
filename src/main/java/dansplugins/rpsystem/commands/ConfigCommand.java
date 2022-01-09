package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.services.LocalConfigService;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class ConfigCommand extends AbstractPluginCommand {

    public ConfigCommand() {
        super(new ArrayList<>(Arrays.asList("config")), new ArrayList<>(Arrays.asList("rp.config")));
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Valid subcommands: show, set");
        return false;
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (args[0].equalsIgnoreCase("show")) {
            LocalConfigService.getInstance().sendConfigList(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (args.length > 2) {

                String option = args[1];
                String value = args[2];

                LocalConfigService.getInstance().setConfigOption(option, value, sender);
                return true;
            }
            else {
                sender.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Usage: /rp config set (option) (value)");
                return false;
            }
        }
        execute(sender);
        return false;
    }
}