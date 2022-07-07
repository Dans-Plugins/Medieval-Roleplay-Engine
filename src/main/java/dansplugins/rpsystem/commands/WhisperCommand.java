package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.utils.ColorChecker;
import dansplugins.rpsystem.utils.Messenger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.misc.ArgumentParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Daniel McCoy Stephenson
 */
public class WhisperCommand extends AbstractPluginCommand {
    private final ColorChecker colorChecker;
    private final MedievalRoleplayEngine medievalRoleplayEngine;
    private final PersistentData persistentData;
    private final Messenger messenger;

    public WhisperCommand(ColorChecker colorChecker, MedievalRoleplayEngine medievalRoleplayEngine, PersistentData persistentData, Messenger messenger) {
        super(new ArrayList<>(Arrays.asList("whisper")), new ArrayList<>(Arrays.asList("rp.whisper")));
        this.colorChecker = colorChecker;
        this.medievalRoleplayEngine = medievalRoleplayEngine;
        this.persistentData = persistentData;
        this.messenger = messenger;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(colorChecker.getNegativeAlertColor() + "Usage: /whisper (message)");
        return false;
    }

    public boolean execute(CommandSender sender, String[] args) {

        int whisperChatRadius = medievalRoleplayEngine.getConfig().getInt("whisperChatRadius");
        String whisperChatColor = medievalRoleplayEngine.getConfig().getString("whisperChatColor");

        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            return execute(sender);
        }

        ArgumentParser argumentParser = new ArgumentParser();
        List<String> doubleQuoteArgs = argumentParser.getArgumentsInsideDoubleQuotes(args);

        if (doubleQuoteArgs.size() == 0) {
            player.sendMessage(colorChecker.getNegativeAlertColor() + "Message must be designated between double quotes.");
            return false;
        }

        String message = doubleQuoteArgs.get(0);

        String formattedMessage = colorChecker.getColorByName(whisperChatColor) + "" + String.format("%s whispers: \"%s\"", persistentData.getCharacter(player.getUniqueId()).getInfo("name"), message);

        int numPlayersWhoHeard = messenger.sendRPMessageToPlayersWithinDistance(player, formattedMessage, whisperChatRadius);

        player.sendMessage(colorChecker.getNeutralAlertColor() + "" + numPlayersWhoHeard + " players heard your whisper.");
        return true;
    }
}