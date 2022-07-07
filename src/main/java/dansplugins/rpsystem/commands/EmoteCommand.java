package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.services.ConfigService;
import dansplugins.rpsystem.utils.ColorChecker;
import dansplugins.rpsystem.utils.Messenger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.misc.ArgumentParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Daniel McCoy Stephenson
 */
public class EmoteCommand extends AbstractPluginCommand {
    private final ColorChecker colorChecker;
    private final PersistentData persistentData;
    private final Messenger messenger;
    private final ConfigService configService;

    public EmoteCommand(ColorChecker colorChecker, PersistentData persistentData, Messenger messenger, ConfigService configService) {
        super(new ArrayList<>(Arrays.asList("emote")), new ArrayList<>(Arrays.asList("rp.emote")));
        this.colorChecker = colorChecker;
        this.persistentData = persistentData;
        this.messenger = messenger;
        this.configService = configService;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.RED + "Usage: /emote \"message\"");
        return false;
    }

    public boolean execute(CommandSender sender, String[] args) {

        int emoteRadius = configService.getInt("emoteRadius");
        String emoteColor = configService.getString("emoteColor");

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