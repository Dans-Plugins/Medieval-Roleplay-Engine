package dansplugins.rpsystem.commands.yell;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class YellCommand {
    private final MedievalRoleplayEngine medievalRoleplayEngine;

    public YellCommand(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    public void sendLoudMessage(CommandSender sender, String[] args) {

        int yellChatRadius = medievalRoleplayEngine.getConfig().getInt("yellChatRadius");
        String yellChatColor = medievalRoleplayEngine.getConfig().getString("yellChatColor");

        // player check
        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        if (player.hasPermission("rp.yell") || player.hasPermission("rp.default")) {

            if (args.length > 0) {
                String message = medievalRoleplayEngine.colorChecker.getColorByName(yellChatColor) + "" + String.format("%s yells: \"%s\"", medievalRoleplayEngine.cardRepository.getCard(player.getUniqueId()).getName(), medievalRoleplayEngine.argumentParser.createStringFromArgs(args));

                medievalRoleplayEngine.messenger.sendRPMessageToPlayersWithinDistance(player, message, yellChatRadius);
            }
            else {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Usage: /yell (message)");
            }

        }
        else {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.yell'");
        }

    }

}
