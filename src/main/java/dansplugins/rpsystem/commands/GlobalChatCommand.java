package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.data.EphemeralData;
import dansplugins.rpsystem.managers.ConfigManager;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

public class GlobalChatCommand extends AbstractCommand {
    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("global"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("rp.global"));

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

        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (!ConfigManager.getInstance().getBoolean("legacyChat")) {

            if (args.length != 0) {
                if (args[0].equalsIgnoreCase("hide")) {
                    addToPlayersWhoHaveHiddenGlobalChat(player);
                    return true;
                }
                if (args[0].equalsIgnoreCase("show")) {
                    removeFromPlayersWhoHaveHiddenGlobalChat(player);
                    return true;
                }
            }

        }

        // remove player from local chat
        removePlayerFromLocalChat(player);

        return true;
    }

    private void removePlayerFromLocalChat(Player player) {
        if (EphemeralData.getInstance().getPlayersSpeakingInLocalChat().contains(player.getUniqueId())) {
            EphemeralData.getInstance().getPlayersSpeakingInLocalChat().remove(player.getUniqueId());
            player.sendMessage(ColorChecker.getInstance().getPositiveAlertColor() + "You are now talking in global chat.");
        }
        else {
            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "You're already talking in global chat!");
        }
    }

    private void addToPlayersWhoHaveHiddenGlobalChat(Player player) {
        if (!EphemeralData.getInstance().getPlayersWhoHaveHiddenGlobalChat().contains(player.getUniqueId())) {
            EphemeralData.getInstance().getPlayersWhoHaveHiddenGlobalChat().add(player.getUniqueId());
            player.sendMessage(ColorChecker.getInstance().getPositiveAlertColor() + "Global chat is now hidden!");
        }
        else {
            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Global chat is already hidden!");
        }
    }

    private void removeFromPlayersWhoHaveHiddenGlobalChat(Player player) {
        if (EphemeralData.getInstance().getPlayersWhoHaveHiddenGlobalChat().contains(player.getUniqueId())) {
            EphemeralData.getInstance().getPlayersWhoHaveHiddenGlobalChat().remove(player.getUniqueId());
            player.sendMessage(ColorChecker.getInstance().getPositiveAlertColor() + "Global chat is now visible!");
        }
        else {
            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Global chat is already visible!");
        }
    }

}
