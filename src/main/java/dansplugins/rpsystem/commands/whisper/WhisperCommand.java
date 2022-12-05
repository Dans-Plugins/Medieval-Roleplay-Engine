package dansplugins.rpsystem.commands.whisper;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WhisperCommand {
    private final MedievalRoleplayEngine medievalRoleplayEngine;

    public WhisperCommand(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    public void sendQuietMessage(CommandSender sender, String[] args) {

        int whisperChatRadius = medievalRoleplayEngine.getConfig().getInt("whisperChatRadius");
        String whisperChatColor = medievalRoleplayEngine.getConfig().getString("whisperChatColor");

        // player check
        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        if (player.hasPermission("rp.whisper") || player.hasPermission("rp.default")) {

            if (args.length > 0) {
                String message = medievalRoleplayEngine.colorChecker.getColorByName(whisperChatColor) + "" + String.format("%s whispers: \"%s\"", medievalRoleplayEngine.cardRepository.getCard(player.getUniqueId()).getName(), medievalRoleplayEngine.argumentParser.createStringFromArgs(args));

                int numPlayersWhoHeard = medievalRoleplayEngine.messenger.sendRPMessageToPlayersWithinDistance(player, message, whisperChatRadius);

                player.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "" + numPlayersWhoHeard + " players heard your whisper.");
            }
            else {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Usage: /whisper (message)");
            }

        }
        else {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.whisper'");
        }

    }

}
