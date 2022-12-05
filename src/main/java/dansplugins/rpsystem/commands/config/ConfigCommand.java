package dansplugins.rpsystem.commands.config;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConfigCommand {
    private final MedievalRoleplayEngine medievalRoleplayEngine;

    public ConfigCommand(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    public boolean handleConfigAccess(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;

        if (!(player.hasPermission("rp.config") || player.hasPermission("rp.admin"))) {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "You don't have permission to configure Medieval Roleplay Engine!");
            return false;
        }

        if (args.length < 1) {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Valid subcommands: show, set");
            return false;
        }

        if (args[0].equalsIgnoreCase("show")) {
            // no further arguments needed, list config
            medievalRoleplayEngine.configService.sendPlayerConfigList(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {

            // two more arguments needed
            if (args.length > 2) {

                String option = args[1];
                String value = args[2];

                medievalRoleplayEngine.configService.setConfigOption(option, value, player);
                return true;
            }
            else {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Usage: /rpconfig set (option) (value)");
                return false;
            }

        }

        player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Valid subcommands: show, set");

        return false;
    }

}
