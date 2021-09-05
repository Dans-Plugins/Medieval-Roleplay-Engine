package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.ConfigManager;
import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.Messenger;
import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.utils.ArgumentParser;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EmoteCommand {

    public boolean emoteAction(CommandSender sender, String[] args) {

        int emoteRadius = MedievalRoleplayEngine.getInstance().getConfig().getInt("emoteRadius");
        String emoteColor = MedievalRoleplayEngine.getInstance().getConfig().getString("emoteColor");

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("rp.emote") || player.hasPermission("rp.me") || player.hasPermission("rp.default")) {
                if (args.length > 0) {
                    String message = ArgumentParser.getInstance().createStringFromFirstArgOnwards(args, 0);
                    String characterName = PersistentData.getInstance().getCard(player.getUniqueId()).getName();

                    Messenger.getInstance().sendRPMessageToPlayersWithinDistance(player, ColorChecker.getInstance().getColorByName(emoteColor) + "" + ChatColor.ITALIC + characterName + " " + message, emoteRadius);
                }
            }
            else {
                player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("negativeAlertColor")) + "Sorry! In order to use this command, you need one the following permissions: 'rp.emote', 'rp.me'");
                return false;
            }

        }
        return false;
    }

}
