package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.data.EphemeralData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LocalChatCommand {

    public boolean startChattingInLocalChat(CommandSender sender) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("rp.local") || player.hasPermission("rp.rp") || player.hasPermission("rp.default")) {
                if (!EphemeralData.getInstance().getPlayersSpeakingInLocalChat().contains(player.getUniqueId())) {
                    EphemeralData.getInstance().getPlayersSpeakingInLocalChat().add(player.getUniqueId());
                    player.sendMessage(ChatColor.GREEN + "You are now talking in local chat.");
                    return true;
                }
                else {
                    player.sendMessage(ChatColor.RED + "You're already talking in local chat!");
                    return false;
                }
            }
            else {
                player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need one the following permissions: 'rp.local', 'rp.rp'");
                return false;
            }

        }
        return false;
    }

}
