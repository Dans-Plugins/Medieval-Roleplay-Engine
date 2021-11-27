package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.data.EphemeralData;
import dansplugins.rpsystem.integrators.MedievalFactionsIntegrator;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

public class LocalChatCommand extends AbstractCommand {
    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("local"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("rp.local"));

    @Override
    public ArrayList<String> getNames() {
        return names;
    }

    @Override
    public ArrayList<String> getPermissions() {
        return permissions;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        // TODO: implement
        return false;
    }

    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

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

        // add player to local chat
        addPlayerToLocalChat(player);

        return true;
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
