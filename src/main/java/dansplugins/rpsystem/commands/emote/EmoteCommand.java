package dansplugins.rpsystem.commands.emote;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EmoteCommand {
    private final MedievalRoleplayEngine medievalRoleplayEngine;

    public EmoteCommand(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    public boolean emoteAction(CommandSender sender, String[] args) {

        int emoteRadius = medievalRoleplayEngine.getConfig().getInt("emoteRadius");
        String emoteColor = medievalRoleplayEngine.getConfig().getString("emoteColor");

        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (!(player.hasPermission("rp.emote") || player.hasPermission("rp.me") || player.hasPermission("rp.default"))) {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need one of the following permissions: 'rp.emote', 'rp.me'");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Usage: /emote (action)");
            return true;
        }

        String message = medievalRoleplayEngine.argumentParser.createStringFromFirstArgOnwards(args, 0);
        String characterName = medievalRoleplayEngine.cardRepository.getCard(player.getUniqueId()).getName();

        medievalRoleplayEngine.messenger.sendRPMessageToPlayersWithinDistance(player, medievalRoleplayEngine.colorChecker.getColorByName(emoteColor) + "" + ChatColor.ITALIC + characterName + " " + message, emoteRadius);

        return true;
    }

}
