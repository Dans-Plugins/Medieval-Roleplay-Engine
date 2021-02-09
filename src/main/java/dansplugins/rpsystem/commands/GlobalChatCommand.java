package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.data.EphemeralData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GlobalChatCommand {

    public boolean startChattingInGlobalChat(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (!(player.hasPermission("rp.global") || player.hasPermission("rp.ooc") || player.hasPermission("rp.default"))) {
            player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need one the following permissions: 'rp.global', 'rp.ooc'");
            return false;
        }

        if (args.length != 0) {
            if (args[0].equalsIgnoreCase("leave")) {
                addToPlayersWhoHaveLeftGlobalChat(player);
                return true;
            }
            if (args[0].equalsIgnoreCase("join")) {
                removeFromPlayersWhoHaveLeftGlobalChat(player);
                return true;
            }
        }


        removePlayerFromLocalChat(player);
        return true;
    }

    private void removePlayerFromLocalChat(Player player) {
        if (EphemeralData.getInstance().getPlayersSpeakingInLocalChat().contains(player.getUniqueId())) {
            EphemeralData.getInstance().getPlayersSpeakingInLocalChat().remove(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "You are now talking in global chat.");
        }
        else {
            player.sendMessage(ChatColor.RED + "You're already talking in global chat!");
        }
    }

    private void addToPlayersWhoHaveLeftGlobalChat(Player player) {
        if (!EphemeralData.getInstance().getPlayersWhoHaveLeftGlobalChat().contains(player.getUniqueId())) {
            EphemeralData.getInstance().getPlayersWhoHaveLeftGlobalChat().add(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "You have left global chat!");
        }
        else {
            player.sendMessage(ChatColor.RED + "You have already left global chat!");
        }
    }

    private void removeFromPlayersWhoHaveLeftGlobalChat(Player player) {
        if (EphemeralData.getInstance().getPlayersWhoHaveLeftGlobalChat().contains(player.getUniqueId())) {
            EphemeralData.getInstance().getPlayersWhoHaveLeftGlobalChat().remove(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "You have rejoined global chat!");
        }
        else {
            player.sendMessage(ChatColor.RED + "You are already receiving messages from the global chat!");
        }
    }

}
