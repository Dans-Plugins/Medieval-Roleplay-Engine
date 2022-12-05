package dansplugins.rpsystem.commands.help;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpCommand {
    private final MedievalRoleplayEngine medievalRoleplayEngine;

    public HelpCommand(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    public void showListOfCommands(CommandSender sender) {

        // player check
        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        if (player.hasPermission("rp.rphelp") || player.hasPermission("rp.default")) {

            player.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + " == Medieval Roleplay Engine " + medievalRoleplayEngine.getVersion() + " Commands == ");
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "/rphelp - Show a list of useful commands for the plugin.");
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "/card help - Show a list of commands useful for managing character cards.");
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "/bird - Send a bird to another player.");
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "/roll or /dice - Roll a dice.");
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "/title - Rename an unwritten book.");

            if (medievalRoleplayEngine.configService.getBoolean("chatFeaturesEnabled")) {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "/rp - Enter local (RP) chat.");
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "/ooc - Enter global (OOC) chat.");
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "/emote or /me - Send an emote to nearby players.");
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "/yell (message)- Send a single messages to far away players.");
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "/whisper (message) - Send a single message to very close players.");
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "/lo (message) - Send an single OOC message to nearby players.");
            }

            player.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "/rpconfig - View and change config options.");

        }
        else {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.rphelp'");
        }

    }

}
