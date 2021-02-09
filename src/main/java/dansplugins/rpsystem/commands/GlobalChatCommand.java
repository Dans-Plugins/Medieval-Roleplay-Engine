package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.data.EphemeralData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GlobalChatCommand {

    public boolean startChattingInGlobalChat(CommandSender sender) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("rp.global") || player.hasPermission("rp.ooc") || player.hasPermission("rp.default")) {
                if (EphemeralData.getInstance().getPlayersSpeakingInLocalChat().contains(player.getUniqueId())) {
                    EphemeralData.getInstance().getPlayersSpeakingInLocalChat().remove(player.getUniqueId());
                    player.sendMessage(ChatColor.GREEN + "You are now talking in global chat.");
                }
                else {
                    player.sendMessage(ChatColor.RED + "You're already talking in global chat!");
                }
            }
            else {
                player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need one the following permissions: 'rp.global', 'rp.ooc'");
            }

        }
        return false;
    }

}
