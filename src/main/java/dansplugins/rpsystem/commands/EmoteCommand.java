package dansplugins.rpsystem.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.utils.ColorChecker;
import dansplugins.rpsystem.utils.Messenger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.misc.ArgumentParser;

/**
 * @author Daniel McCoy Stephenson
 */
public class EmoteCommand extends AbstractPluginCommand {
    private final MedievalRoleplayEngine medievalRoleplayEngine;
    private final ColorChecker colorChecker;
    private final PersistentData persistentData;
    private final Messenger messenger;

    public EmoteCommand(MedievalRoleplayEngine medievalRoleplayEngine, ColorChecker colorChecker, PersistentData persistentData, Messenger messenger) {
        super(new ArrayList<>(Arrays.asList("emote")), new ArrayList<>(Arrays.asList("rp.emote")));
        this.medievalRoleplayEngine = medievalRoleplayEngine;
        this.colorChecker = colorChecker;
        this.persistentData = persistentData;
        this.messenger = messenger;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.RED + "Usage: /emote \"message\"");
        return false;
    }

    public boolean execute(CommandSender sender, String[] args) {

        int emoteRadius = medievalRoleplayEngine.getConfig().getInt("emoteRadius");
        String emoteColor = medievalRoleplayEngine.getConfig().getString("emoteColor");

        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            return execute(sender);
        }

        ArgumentParser argumentParser = new ArgumentParser();
        List<String> doubleQuoteArgs = argumentParser.getArgumentsInsideDoubleQuotes(args);

        if (doubleQuoteArgs.size() == 0) {
            player.sendMessage(colorChecker.getNegativeAlertColor() + "Message must be designated between double quotes.");
            return false;
        }

        String message = doubleQuoteArgs.get(0);

        String characterName = persistentData.getCharacter(player.getUniqueId()).getInfo("name");

        messenger.sendRPMessageToPlayersWithinDistance(player, colorChecker.getColorByName(emoteColor) + "" + ChatColor.ITALIC + characterName + " " + message, emoteRadius);
        return true;
    }
}