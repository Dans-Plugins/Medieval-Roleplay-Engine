package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.data.EphemeralData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LocalChatCommand {

    public boolean startChattingInLocalChat(CommandSender sender, String[] args) {

        if (sender instanceof Player) {
            return false;
        }

        Player player = (Player) sender;

        if (!(player.hasPermission("rp.local") || player.hasPermission("rp.rp") || player.hasPermission("rp.default"))) {
            player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need one the following permissions: 'rp.local', 'rp.rp'");
            return false;
        }

        if (args.length != 0) {
            if (args[0].equalsIgnoreCase("hide")) {
                addToPlayersWhoHaveHiddenLocalChat(player);
                return true;
            }
            if (args[0].equalsIgnoreCase("show")) {
                removeFromPlayersWhoHaveHiddenLocalChat(player);
                return true;
            }
        }

        addPlayerToLocalChat(player);
        return true;
    }

    private void addPlayerToLocalChat(Player player) {
        if (!EphemeralData.getInstance().getPlayersSpeakingInLocalChat().contains(player.getUniqueId())) {
            EphemeralData.getInstance().getPlayersSpeakingInLocalChat().add(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "You are now talking in local chat.");
        }
        else {
            player.sendMessage(ChatColor.RED + "You're already talking in local chat!");
        }
    }

    private void addToPlayersWhoHaveHiddenLocalChat(Player player) {
        if (!EphemeralData.getInstance().getPlayersWhoHaveHiddenLocalChat().contains(player.getUniqueId())) {
            EphemeralData.getInstance().getPlayersWhoHaveHiddenLocalChat().add(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Local chat is now hidden!");
        }
        else {
            player.sendMessage(ChatColor.RED + "Local chat is already hidden!");
        }
    }

    private void removeFromPlayersWhoHaveHiddenLocalChat(Player player) {
        if (EphemeralData.getInstance().getPlayersWhoHaveHiddenLocalChat().contains(player.getUniqueId())) {
            EphemeralData.getInstance().getPlayersWhoHaveHiddenLocalChat().remove(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Local chat is now visible!");
        }
        else {
            player.sendMessage(ChatColor.RED + "Local chat is already visible!");
        }
    }

}
