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

public class YellCommand extends AbstractCommand {
    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("lo"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("rp.lo"));

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
        commandSender.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Usage: /yell (message)");
        return false;
    }

    public boolean execute(CommandSender sender, String[] args) {

        int yellChatRadius = MedievalRoleplayEngine.getInstance().getConfig().getInt("yellChatRadius");
        String yellChatColor = MedievalRoleplayEngine.getInstance().getConfig().getString("yellChatColor");

        // player check
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            return execute(sender);
        }

        String message = ColorChecker.getInstance().getColorByName(yellChatColor) + "" + String.format("%s yells: \"%s\"", PersistentData.getInstance().getCard(player.getUniqueId()).getName(), ArgumentParser.getInstance().createStringFromArgs(args));

        Messenger.getInstance().sendRPMessageToPlayersWithinDistance(player, message, yellChatRadius);
        return true;

    }

}
