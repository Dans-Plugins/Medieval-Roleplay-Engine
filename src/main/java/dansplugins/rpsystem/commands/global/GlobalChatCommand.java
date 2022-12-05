package dansplugins.rpsystem.commands.global;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GlobalChatCommand {
    private final MedievalRoleplayEngine medievalRoleplayEngine;

    public GlobalChatCommand(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    public boolean startChattingInGlobalChat(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (!(player.hasPermission("rp.global") || player.hasPermission("rp.ooc") || player.hasPermission("rp.default"))) {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need one the following permissions: 'rp.global', 'rp.ooc'");
            return false;
        }

        if (!medievalRoleplayEngine.configService.getBoolean("legacyChat")) {

            if (args.length != 0) {
                if (args[0].equalsIgnoreCase("hide")) {
                    addToPlayersWhoHaveHiddenGlobalChat(player);
                    return true;
                }
                if (args[0].equalsIgnoreCase("show")) {
                    removeFromPlayersWhoHaveHiddenGlobalChat(player);
                    return true;
                }
            }

        }

        // remove player from local chat
        removePlayerFromLocalChat(player);

        return true;
    }

    private void removePlayerFromLocalChat(Player player) {
        if (medievalRoleplayEngine.ephemeralData.getPlayersSpeakingInLocalChat().contains(player.getUniqueId())) {
            medievalRoleplayEngine.ephemeralData.getPlayersSpeakingInLocalChat().remove(player.getUniqueId());
            player.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor() + "You are now talking in global chat.");
        }
        else {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "You're already talking in global chat!");
        }
    }

    private void addToPlayersWhoHaveHiddenGlobalChat(Player player) {
        if (!medievalRoleplayEngine.ephemeralData.getPlayersWhoHaveHiddenGlobalChat().contains(player.getUniqueId())) {
            medievalRoleplayEngine.ephemeralData.getPlayersWhoHaveHiddenGlobalChat().add(player.getUniqueId());
            player.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor() + "Global chat is now hidden!");
        }
        else {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Global chat is already hidden!");
        }
    }

    private void removeFromPlayersWhoHaveHiddenGlobalChat(Player player) {
        if (medievalRoleplayEngine.ephemeralData.getPlayersWhoHaveHiddenGlobalChat().contains(player.getUniqueId())) {
            medievalRoleplayEngine.ephemeralData.getPlayersWhoHaveHiddenGlobalChat().remove(player.getUniqueId());
            player.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor() + "Global chat is now visible!");
        }
        else {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Global chat is already visible!");
        }
    }

}
