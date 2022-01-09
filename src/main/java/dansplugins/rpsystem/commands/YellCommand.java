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

/**
 * @author Daniel McCoy Stephenson
 */
public class YellCommand extends AbstractCommand {
    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("yell"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("rp.yell"));

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

        ArrayList<String> doubleQuoteArgs = MedievalRoleplayEngine.getInstance().getToolbox().getArgumentParser().getArgumentsInsideDoubleQuotes(args);

        if (doubleQuoteArgs.size() == 0) {
            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Message must be designated between double quotes.");
            return false;
        }

        String message = doubleQuoteArgs.get(0);

        String formattedMessage = ColorChecker.getInstance().getColorByName(yellChatColor) + "" + String.format("%s yells: \"%s\"", PersistentData.getInstance().getCharacter(player.getUniqueId()).getInfo("name"), message);

        Messenger.getInstance().sendRPMessageToPlayersWithinDistance(player, formattedMessage, yellChatRadius);
        return true;
    }
}