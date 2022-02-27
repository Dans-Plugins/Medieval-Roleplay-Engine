package dansplugins.rpsystem.commands;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dansplugins.rpsystem.data.EphemeralData;
import dansplugins.rpsystem.services.LocalConfigService;
import dansplugins.rpsystem.utils.ColorChecker;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

/**
 * @author Daniel McCoy Stephenson
 */
public class GlobalChatCommand extends AbstractPluginCommand {

    public GlobalChatCommand() {
        super(new ArrayList<>(Arrays.asList("global")), new ArrayList<>(Arrays.asList("rp.global")));
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;

        // remove player from local chat
        removePlayerFromLocalChat(player);
        return true;
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;

        if (!LocalConfigService.getInstance().getBoolean("legacyChat")) {

            if (args[0].equalsIgnoreCase("hide")) {
                addToPlayersWhoHaveHiddenGlobalChat(player);
                return true;
            }
            if (args[0].equalsIgnoreCase("show")) {
                removeFromPlayersWhoHaveHiddenGlobalChat(player);
                return true;
            }

            player.sendMessage(ChatColor.RED + "Usage: /rp global <[ show | hide ]>");
            return false;
        }
        else {
            return execute(sender);
        }
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