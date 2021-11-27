package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.Messenger;
import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.utils.ArgumentParser;
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
        // TODO: implement
        return false;
    }

    public boolean execute(CommandSender sender, String[] args) {

        int whisperChatRadius = MedievalRoleplayEngine.getInstance().getConfig().getInt("whisperChatRadius");
        String whisperChatColor =MedievalRoleplayEngine.getInstance().getConfig().getString("whisperChatColor");

        // player check
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length > 0) {
            String message = ColorChecker.getInstance().getColorByName(whisperChatColor) + "" + String.format("%s whispers: \"%s\"", PersistentData.getInstance().getCard(player.getUniqueId()).getName(), ArgumentParser.getInstance().createStringFromArgs(args));

            int numPlayersWhoHeard = Messenger.getInstance().sendRPMessageToPlayersWithinDistance(player, message, whisperChatRadius);

            player.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "" + numPlayersWhoHeard + " players heard your whisper.");
        }
        else {
            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Usage: /whisper (message)");
        }
        return true;
    }

}
