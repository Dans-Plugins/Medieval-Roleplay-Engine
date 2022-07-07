package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.data.EphemeralData;
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
public class LocalChatCommand extends AbstractPluginCommand {
    private final EphemeralData ephemeralData;
    private final ColorChecker colorChecker;

    public LocalChatCommand(EphemeralData ephemeralData, ColorChecker colorChecker) {
        super(new ArrayList<>(Arrays.asList("local")), new ArrayList<>(Arrays.asList("mre.local")));
        this.ephemeralData = ephemeralData;
        this.colorChecker = colorChecker;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;

        // add player to local chat
        addPlayerToLocalChat(player);
        return true;
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if (args[0].equalsIgnoreCase("hide")) {
            addToPlayersWhoHaveHiddenLocalChat(player);
            return true;
        }
        if (args[0].equalsIgnoreCase("show")) {
            removeFromPlayersWhoHaveHiddenLocalChat(player);
            return true;
        }

        player.sendMessage(ChatColor.RED + "Usage: /mre local <[ show | hide ]>");
        return false;
    }

    private void addPlayerToLocalChat(Player player) {
        if (!ephemeralData.getPlayersSpeakingInLocalChat().contains(player.getUniqueId())) {
            ephemeralData.getPlayersSpeakingInLocalChat().add(player.getUniqueId());
            player.sendMessage(colorChecker.getPositiveAlertColor() + "You are now talking in local chat.");
        }
        else {
            player.sendMessage(colorChecker.getPositiveAlertColor() + "You're already in local chat.");
        }
    }

    private void addToPlayersWhoHaveHiddenLocalChat(Player player) {
        if (!ephemeralData.getPlayersWhoHaveHiddenLocalChat().contains(player.getUniqueId())) {
            ephemeralData.getPlayersWhoHaveHiddenLocalChat().add(player.getUniqueId());
            player.sendMessage(colorChecker.getPositiveAlertColor() + "Local chat is now hidden!");
        }
        else {
            player.sendMessage(colorChecker.getNegativeAlertColor() + "Local chat is already hidden!");
        }
    }

    private void removeFromPlayersWhoHaveHiddenLocalChat(Player player) {
        if (ephemeralData.getPlayersWhoHaveHiddenLocalChat().contains(player.getUniqueId())) {
            ephemeralData.getPlayersWhoHaveHiddenLocalChat().remove(player.getUniqueId());
            player.sendMessage(colorChecker.getPositiveAlertColor() + "Local chat is now visible!");
        }
        else {
            player.sendMessage(colorChecker.getNegativeAlertColor() + "Local chat is already visible!");
        }
    }
}