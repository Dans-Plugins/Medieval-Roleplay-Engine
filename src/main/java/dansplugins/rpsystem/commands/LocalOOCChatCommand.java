package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.Messenger;
import dansplugins.rpsystem.data.EphemeralData;
import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

public class LocalOOCChatCommand extends AbstractCommand {
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
        commandSender.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Usage: /lo (message)");
        return false;
    }

    public boolean execute(CommandSender sender, String[] args) {
        int localOOCChatRadius = MedievalRoleplayEngine.getInstance().getConfig().getInt("localOOCChatRadius");
        String localOOCChatColor = MedievalRoleplayEngine.getInstance().getConfig().getString("localOOCChatColor");

        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            return execute(sender);
        }
        
        if (args[0].equalsIgnoreCase("hide")) {
            addToPlayersWhoHaveHiddenLocalOOCChat(player);
        }
        if (args[0].equalsIgnoreCase("show")) {
            removeFromPlayersWhoHaveHiddenLocalOOCChat(player);
        }

        ArrayList<String> doubleQuoteArgs = MedievalRoleplayEngine.getInstance().getToolbox().getArgumentParser().getArgumentsInsideDoubleQuotes(args);

        if (doubleQuoteArgs.size() == 0) {
            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Message must be designated between double quotes.");
            return false;
        }

        String message = doubleQuoteArgs.get(0);
        
        String formatted = ColorChecker.getInstance().getColorByName(localOOCChatColor) + "" + String.format("<%s> (( %s ))", PersistentData.getInstance().getCard(player.getUniqueId()).getName(), message);

        Messenger.getInstance().sendOOCMessageToPlayersWithinDistance(player, formatted, localOOCChatRadius);
        return true;
    }

    private void addToPlayersWhoHaveHiddenLocalOOCChat(Player player) {
        if (!EphemeralData.getInstance().getPlayersWhoHaveHiddenLocalOOCChat().contains(player.getUniqueId())) {
            EphemeralData.getInstance().getPlayersWhoHaveHiddenLocalOOCChat().add(player.getUniqueId());
            player.sendMessage(ColorChecker.getInstance().getPositiveAlertColor() + "Local OOC Chat is now hidden!");
        }
        else {
            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Local OOC Chat is already hidden!");
        }
    }

    private void removeFromPlayersWhoHaveHiddenLocalOOCChat(Player player) {
        if (EphemeralData.getInstance().getPlayersWhoHaveHiddenLocalOOCChat().contains(player.getUniqueId())) {
            EphemeralData.getInstance().getPlayersWhoHaveHiddenLocalOOCChat().remove(player.getUniqueId());
            player.sendMessage(ColorChecker.getInstance().getPositiveAlertColor() + "Local OOC Chat is now visible!");
        }
        else {
            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Local OOC Chat is already visible!");
        }
    }

}
