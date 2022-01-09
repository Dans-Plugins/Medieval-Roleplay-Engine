package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.utils.ColorChecker;
import dansplugins.rpsystem.utils.Messenger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.abs.AbstractPluginCommand;
import preponderous.ponder.misc.ArgumentParser;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class EmoteCommand extends AbstractPluginCommand {

    public EmoteCommand() {
        super(new ArrayList<>(Arrays.asList("emote")), new ArrayList<>(Arrays.asList("rp.emote")));
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.RED + "Usage: /emote \"message\"");
        return false;
    }

    public boolean execute(CommandSender sender, String[] args) {

        int emoteRadius = MedievalRoleplayEngine.getInstance().getConfig().getInt("emoteRadius");
        String emoteColor = MedievalRoleplayEngine.getInstance().getConfig().getString("emoteColor");

        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            return execute(sender);
        }

        ArgumentParser argumentParser = new ArgumentParser();
        ArrayList<String> doubleQuoteArgs = argumentParser.getArgumentsInsideDoubleQuotes(args);

        if (doubleQuoteArgs.size() == 0) {
            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Message must be designated between double quotes.");
            return false;
        }

        String message = doubleQuoteArgs.get(0);

        String characterName = PersistentData.getInstance().getCharacter(player.getUniqueId()).getInfo("name");

        Messenger.getInstance().sendRPMessageToPlayersWithinDistance(player, ColorChecker.getInstance().getColorByName(emoteColor) + "" + ChatColor.ITALIC + characterName + " " + message, emoteRadius);
        return true;
    }
}