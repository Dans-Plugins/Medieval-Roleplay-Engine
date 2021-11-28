package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.utils.Messenger;
import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

public class WhisperCommand extends AbstractCommand {
    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("whisper"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("rp.whisper"));

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
        commandSender.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Usage: /whisper (message)");
        return false;
    }

    public boolean execute(CommandSender sender, String[] args) {

        int whisperChatRadius = MedievalRoleplayEngine.getInstance().getConfig().getInt("whisperChatRadius");
        String whisperChatColor =MedievalRoleplayEngine.getInstance().getConfig().getString("whisperChatColor");

        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            return execute(sender);
        }

        ArrayList<String> doubleQuoteArgs = MedievalRoleplayEngine.getInstance().getToolbox().getArgumentParser().getArgumentsInsideDoubleQuotes(args);

        if (doubleQuoteArgs.size() == 0) {
            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Message must be designated between double quotes.");
            return false;
        }

        String message = doubleQuoteArgs.get(0);

        String formattedMessage = ColorChecker.getInstance().getColorByName(whisperChatColor) + "" + String.format("%s whispers: \"%s\"", PersistentData.getInstance().getCharacter(player.getUniqueId()).getInfo("name"), message);

        int numPlayersWhoHeard = Messenger.getInstance().sendRPMessageToPlayersWithinDistance(player, formattedMessage, whisperChatRadius);

        player.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "" + numPlayersWhoHeard + " players heard your whisper.");
        return true;
    }

}
