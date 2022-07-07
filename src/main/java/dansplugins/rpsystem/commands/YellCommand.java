package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.services.ConfigService;
import dansplugins.rpsystem.utils.ColorChecker;
import dansplugins.rpsystem.utils.Messenger;
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
public class YellCommand extends AbstractPluginCommand {
    private final ColorChecker colorChecker;
    private final PersistentData persistentData;
    private final Messenger messenger;
    private final ConfigService configService;

    public YellCommand(ColorChecker colorChecker, PersistentData persistentData, Messenger messenger, ConfigService configService) {
        super(new ArrayList<>(Arrays.asList("yell")), new ArrayList<>(Arrays.asList("rp.yell")));
        this.colorChecker = colorChecker;
        this.persistentData = persistentData;
        this.messenger = messenger;
        this.configService = configService;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(colorChecker.getNegativeAlertColor() + "Usage: /yell (message)");
        return false;
    }

    public boolean execute(CommandSender sender, String[] args) {

        int yellChatRadius = configService.getInt("yellChatRadius");
        String yellChatColor = configService.getString("yellChatColor");

        // player check
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

        String formattedMessage = colorChecker.getColorByName(yellChatColor) + "" + String.format("%s yells: \"%s\"", persistentData.getCharacter(player.getUniqueId()).getInfo("name"), message);

        messenger.sendRPMessageToPlayersWithinDistance(player, formattedMessage, yellChatRadius);
        return true;
    }
}