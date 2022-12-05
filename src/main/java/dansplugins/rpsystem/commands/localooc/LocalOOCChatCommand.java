package dansplugins.rpsystem.commands.localooc;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LocalOOCChatCommand {
    private final MedievalRoleplayEngine medievalRoleplayEngine;

    public LocalOOCChatCommand(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    public void sendLocalOOCMessage(CommandSender sender, String[] args) {

        int localOOCChatRadius = medievalRoleplayEngine.getConfig().getInt("localOOCChatRadius");
        String localOOCChatColor = medievalRoleplayEngine.getConfig().getString("localOOCChatColor");

        // player check
        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        if (!(player.hasPermission("rp.localOOC") || player.hasPermission("rp.default"))) {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.yell'");
            return;
        }

        if (args.length == 0) {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Usage: /lo (message)");
            return;
        }
        
        if (args[0].equalsIgnoreCase("hide")) {
            addToPlayersWhoHaveHiddenLocalOOCChat(player);
        }
        if (args[0].equalsIgnoreCase("show")) {
            removeFromPlayersWhoHaveHiddenLocalOOCChat(player);
        }
        
        String message = medievalRoleplayEngine.colorChecker.getColorByName(localOOCChatColor) + "" + String.format("<%s> (( %s ))", medievalRoleplayEngine.cardRepository.getCard(player.getUniqueId()).getName(), medievalRoleplayEngine.argumentParser.createStringFromArgs(args));

        medievalRoleplayEngine.messenger.sendOOCMessageToPlayersWithinDistance(player, message, localOOCChatRadius);
    }

    private void addToPlayersWhoHaveHiddenLocalOOCChat(Player player) {
        if (!medievalRoleplayEngine.ephemeralData.getPlayersWhoHaveHiddenLocalOOCChat().contains(player.getUniqueId())) {
            medievalRoleplayEngine.ephemeralData.getPlayersWhoHaveHiddenLocalOOCChat().add(player.getUniqueId());
            player.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor() + "Local OOC Chat is now hidden!");
        }
        else {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Local OOC Chat is already hidden!");
        }
    }

    private void removeFromPlayersWhoHaveHiddenLocalOOCChat(Player player) {
        if (medievalRoleplayEngine.ephemeralData.getPlayersWhoHaveHiddenLocalOOCChat().contains(player.getUniqueId())) {
            medievalRoleplayEngine.ephemeralData.getPlayersWhoHaveHiddenLocalOOCChat().remove(player.getUniqueId());
            player.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor() + "Local OOC Chat is now visible!");
        }
        else {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Local OOC Chat is already visible!");
        }
    }

}
