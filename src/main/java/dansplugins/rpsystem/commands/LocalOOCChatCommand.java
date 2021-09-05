package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.Messenger;
import dansplugins.rpsystem.data.EphemeralData;
import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.utils.ArgumentParser;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LocalOOCChatCommand {

    public void sendLocalOOCMessage(CommandSender sender, String[] args) {

        int localOOCChatRadius = MedievalRoleplayEngine.getInstance().getConfig().getInt("localOOCChatRadius");
        String localOOCChatColor = MedievalRoleplayEngine.getInstance().getConfig().getString("localOOCChatColor");

        // player check
        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        if (!(player.hasPermission("rp.localOOC") || player.hasPermission("rp.default"))) {
            player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need the following permission: 'rp.yell'");
            return;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /lo (message)");
            return;
        }
        
        if (args[0].equalsIgnoreCase("hide")) {
            addToPlayersWhoHaveHiddenLocalOOCChat(player);
        }
        if (args[0].equalsIgnoreCase("show")) {
            removeFromPlayersWhoHaveHiddenLocalOOCChat(player);
        }
        
        String message = ColorChecker.getInstance().getColorByName(localOOCChatColor) + "" + String.format("<%s> (( %s ))", PersistentData.getInstance().getCard(player.getUniqueId()).getName(), ArgumentParser.getInstance().createStringFromArgs(args));

        Messenger.getInstance().sendOOCMessageToPlayersWithinDistance(player, message, localOOCChatRadius);
    }

    private void addToPlayersWhoHaveHiddenLocalOOCChat(Player player) {
        if (!EphemeralData.getInstance().getPlayersWhoHaveHiddenLocalOOCChat().contains(player.getUniqueId())) {
            EphemeralData.getInstance().getPlayersWhoHaveHiddenLocalOOCChat().add(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Local OOC Chat is now hidden!");
        }
        else {
            player.sendMessage(ChatColor.RED + "Local OOC Chat is already hidden!");
        }
    }

    private void removeFromPlayersWhoHaveHiddenLocalOOCChat(Player player) {
        if (EphemeralData.getInstance().getPlayersWhoHaveHiddenLocalOOCChat().contains(player.getUniqueId())) {
            EphemeralData.getInstance().getPlayersWhoHaveHiddenLocalOOCChat().remove(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Local OOC Chat is now visible!");
        }
        else {
            player.sendMessage(ChatColor.RED + "Local OOC Chat is already visible!");
        }
    }

}
