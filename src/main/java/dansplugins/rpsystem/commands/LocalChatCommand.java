package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.data.EphemeralData;
import dansplugins.rpsystem.integrators.MedievalFactionsIntegrator;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class LocalChatCommand extends AbstractPluginCommand {

    public LocalChatCommand() {
        super(new ArrayList<>(Arrays.asList("local")), new ArrayList<>(Arrays.asList("rp.local")));
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

        player.sendMessage(ChatColor.RED + "Usage: /rp local <[ show | hide ]>");
        return false;
    }

    private void addPlayerToLocalChat(Player player) {
        if (!EphemeralData.getInstance().getPlayersSpeakingInLocalChat().contains(player.getUniqueId())) {
            EphemeralData.getInstance().getPlayersSpeakingInLocalChat().add(player.getUniqueId());
            if (MedievalFactionsIntegrator.getInstance().isMedievalFactionsPresent() && MedievalFactionsIntegrator.getInstance().getAPI().isPlayerInFactionChat(player)) {
                player.sendMessage(ColorChecker.getInstance().getPositiveAlertColor() + "You are now in local chat, but you won't send messages to local chat until you leave faction chat.");
            }
            else {
                player.sendMessage(ColorChecker.getInstance().getPositiveAlertColor() + "You are now talking in local chat.");
            }
        }
        else {
            if (MedievalFactionsIntegrator.getInstance().isMedievalFactionsPresent() && MedievalFactionsIntegrator.getInstance().getAPI().isPlayerInFactionChat(player)) {
                player.sendMessage(ColorChecker.getInstance().getPositiveAlertColor() + "You're already now in local chat, but you won't send messages to local chat until you leave faction chat.");
            }
            else {
                player.sendMessage(ColorChecker.getInstance().getPositiveAlertColor() + "You're already now in local chat.");
            }
        }
    }

    private void addToPlayersWhoHaveHiddenLocalChat(Player player) {
        if (!EphemeralData.getInstance().getPlayersWhoHaveHiddenLocalChat().contains(player.getUniqueId())) {
            EphemeralData.getInstance().getPlayersWhoHaveHiddenLocalChat().add(player.getUniqueId());
            player.sendMessage(ColorChecker.getInstance().getPositiveAlertColor() + "Local chat is now hidden!");
        }
        else {
            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Local chat is already hidden!");
        }
    }

    private void removeFromPlayersWhoHaveHiddenLocalChat(Player player) {
        if (EphemeralData.getInstance().getPlayersWhoHaveHiddenLocalChat().contains(player.getUniqueId())) {
            EphemeralData.getInstance().getPlayersWhoHaveHiddenLocalChat().remove(player.getUniqueId());
            player.sendMessage(ColorChecker.getInstance().getPositiveAlertColor() + "Local chat is now visible!");
        }
        else {
            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Local chat is already visible!");
        }
    }
}