package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.utils.ColorChecker;
import dansplugins.rpsystem.utils.Messenger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.abs.AbstractPluginCommand;
import preponderous.ponder.misc.ArgumentParser;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class WhisperCommand extends AbstractPluginCommand {

    public WhisperCommand() {
        super(new ArrayList<>(Arrays.asList("whisper")), new ArrayList<>(Arrays.asList("rp.whisper")));
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

        ArgumentParser argumentParser = new ArgumentParser();
        ArrayList<String> doubleQuoteArgs = argumentParser.getArgumentsInsideDoubleQuotes(args);

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