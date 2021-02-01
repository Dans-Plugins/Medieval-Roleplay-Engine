package rpsystem.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rpsystem.ConfigManager;

public class ConfigCommand {

    public boolean handleConfigAccess(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;

        if (!(player.hasPermission("rp.config") || player.hasPermission("rp.admin"))) {
            player.sendMessage(ChatColor.RED + "You don't have permission to configure Medieval Roleplay Engine!");
            return false;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Valid subcommands: show, set");
            return false;
        }

        if (args[1].equalsIgnoreCase("show")) {
            // no further arguments needed, list config
            ConfigManager.getInstance().sendPlayerConfigList(player);
            return true;
        }

        if (args[1].equalsIgnoreCase("set")) {

            // two more arguments needed
            if (args.length > 3) {

                String option = args[2];
                String value = args[3];

                ConfigManager.setConfigOption(option, value, player);
                return true;
            }
            else {
                player.sendMessage(ChatColor.RED + "Usage: /rpconfig set (option) (value)");
                return false;
            }

        }

        player.sendMessage(ChatColor.RED + "Valid subcommands: show, set");

        return false;
    }

}
