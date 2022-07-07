package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.data.EphemeralData;
import dansplugins.rpsystem.services.ConfigService;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class GlobalChatCommand extends AbstractPluginCommand {
    private final ConfigService configService;
    private final EphemeralData ephemeralData;
    private final ColorChecker colorChecker;

    public GlobalChatCommand(ConfigService configService, EphemeralData ephemeralData, ColorChecker colorChecker) {
        super(new ArrayList<>(Arrays.asList("global")), new ArrayList<>(Arrays.asList("rp.global")));
        this.configService = configService;
        this.ephemeralData = ephemeralData;
        this.colorChecker = colorChecker;
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

        if (!configService.getBoolean("legacyChat")) {

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
        if (ephemeralData.getPlayersSpeakingInLocalChat().contains(player.getUniqueId())) {
            ephemeralData.getPlayersSpeakingInLocalChat().remove(player.getUniqueId());
            player.sendMessage(colorChecker.getPositiveAlertColor() + "You are now talking in global chat.");
        }
        else {
            player.sendMessage(colorChecker.getNegativeAlertColor() + "You're already talking in global chat!");
        }
    }

    private void addToPlayersWhoHaveHiddenGlobalChat(Player player) {
        if (!ephemeralData.getPlayersWhoHaveHiddenGlobalChat().contains(player.getUniqueId())) {
            ephemeralData.getPlayersWhoHaveHiddenGlobalChat().add(player.getUniqueId());
            player.sendMessage(colorChecker.getPositiveAlertColor() + "Global chat is now hidden!");
        }
        else {
            player.sendMessage(colorChecker.getNegativeAlertColor() + "Global chat is already hidden!");
        }
    }

    private void removeFromPlayersWhoHaveHiddenGlobalChat(Player player) {
        if (ephemeralData.getPlayersWhoHaveHiddenGlobalChat().contains(player.getUniqueId())) {
            ephemeralData.getPlayersWhoHaveHiddenGlobalChat().remove(player.getUniqueId());
            player.sendMessage(colorChecker.getPositiveAlertColor() + "Global chat is now visible!");
        }
        else {
            player.sendMessage(colorChecker.getNegativeAlertColor() + "Global chat is already visible!");
        }
    }
}