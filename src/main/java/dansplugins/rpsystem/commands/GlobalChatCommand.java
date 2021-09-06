package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.data.EphemeralData;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GlobalChatCommand {

    public boolean startChattingInGlobalChat(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (!(player.hasPermission("rp.global") || player.hasPermission("rp.ooc") || player.hasPermission("rp.default"))) {
            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Sorry! In order to use this command, you need one the following permissions: 'rp.global', 'rp.ooc'");
            return false;
        }

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
