package dansplugins.rpsystem.commands.local;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LocalChatCommand {
    private final MedievalRoleplayEngine medievalRoleplayEngine;

    public LocalChatCommand(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    public boolean startChattingInLocalChat(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (!(player.hasPermission("rp.local") || player.hasPermission("rp.rp") || player.hasPermission("rp.default"))) {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need one the following permissions: 'rp.local', 'rp.rp'");
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

        // add player to local chat
        addPlayerToLocalChat(player);

        return true;
    }

    private void addPlayerToLocalChat(Player player) {
        if (!medievalRoleplayEngine.ephemeralData.getPlayersSpeakingInLocalChat().contains(player.getUniqueId())) {
            medievalRoleplayEngine.ephemeralData.getPlayersSpeakingInLocalChat().add(player.getUniqueId());
            player.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor() + "You are now talking in local chat.");
        }
        else {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor() + "You're already now in local chat.");
        }
    }

    private void addToPlayersWhoHaveHiddenLocalChat(Player player) {
        if (!medievalRoleplayEngine.ephemeralData.getPlayersWhoHaveHiddenLocalChat().contains(player.getUniqueId())) {
            medievalRoleplayEngine.ephemeralData.getPlayersWhoHaveHiddenLocalChat().add(player.getUniqueId());
            player.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor() + "Local chat is now hidden!");
        }
        else {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Local chat is already hidden!");
        }
    }

    private void removeFromPlayersWhoHaveHiddenLocalChat(Player player) {
        if (medievalRoleplayEngine.ephemeralData.getPlayersWhoHaveHiddenLocalChat().contains(player.getUniqueId())) {
            medievalRoleplayEngine.ephemeralData.getPlayersWhoHaveHiddenLocalChat().remove(player.getUniqueId());
            player.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor() + "Local chat is now visible!");
        }
        else {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Local chat is already visible!");
        }
    }

}
