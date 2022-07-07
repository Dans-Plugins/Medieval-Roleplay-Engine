package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.services.ConfigService;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class ConfigCommand extends AbstractPluginCommand {
    private final ColorChecker colorChecker;
    private final ConfigService configService;

    public ConfigCommand(ColorChecker colorChecker, ConfigService configService) {
        super(new ArrayList<>(Arrays.asList("config")), new ArrayList<>(Arrays.asList("rp.config")));
        this.colorChecker = colorChecker;
        this.configService = configService;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(colorChecker.getNegativeAlertColor() + "Valid subcommands: show, set");
        return false;
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (args[0].equalsIgnoreCase("show")) {
            configService.sendConfigList(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (args.length > 2) {

                String option = args[1];
                String value = args[2];

                configService.setConfigOption(option, value, sender);
                return true;
            }
            else {
                sender.sendMessage(colorChecker.getNegativeAlertColor() + "Usage: /rp config set (option) (value)");
                return false;
            }
        }
        execute(sender);
        return false;
    }
}