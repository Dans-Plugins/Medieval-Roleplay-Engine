package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.Messenger;
import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.utils.ArgumentParser;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class YellCommand {

    public void sendLoudMessage(CommandSender sender, String[] args) {

        int yellChatRadius = MedievalRoleplayEngine.getInstance().getConfig().getInt("yellChatRadius");
        String yellChatColor =MedievalRoleplayEngine.getInstance().getConfig().getString("yellChatColor");

        // player check
        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        if (player.hasPermission("rp.yell") || player.hasPermission("rp.default")) {

            if (args.length > 0) {
                String message = ColorChecker.getInstance().getColorByName(yellChatColor) + "" + String.format("%s yells: \"%s\"", PersistentData.getInstance().getCard(player.getUniqueId()).getName(), ArgumentParser.getInstance().createStringFromArgs(args));

                Messenger.getInstance().sendRPMessageToPlayersWithinDistance(player, message, yellChatRadius);
            }
            else {
                player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Usage: /yell (message)");
            }

        }
        else {
            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.yell'");
        }

    }

}
