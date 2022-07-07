package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.data.EphemeralData;
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
public class LocalOOCChatCommand extends AbstractPluginCommand {
    private final ColorChecker colorChecker;
    private final PersistentData persistentData;
    private final Messenger messenger;
    private final EphemeralData ephemeralData;
    private final ConfigService configService;

    public LocalOOCChatCommand(ColorChecker colorChecker, PersistentData persistentData, Messenger messenger, EphemeralData ephemeralData, ConfigService configService) {
        super(new ArrayList<>(Arrays.asList("lo")), new ArrayList<>(Arrays.asList("rp.lo")));
        this.colorChecker = colorChecker;
        this.persistentData = persistentData;
        this.messenger = messenger;
        this.ephemeralData = ephemeralData;
        this.configService = configService;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(colorChecker.getNegativeAlertColor() + "Usage: /lo (message)");
        return false;
    }

    public boolean execute(CommandSender sender, String[] args) {
        int localOOCChatRadius = configService.getInt("localOOCChatRadius");
        String localOOCChatColor = configService.getString("localOOCChatColor");
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            return execute(sender);
        }
        if (args[0].equalsIgnoreCase("hide")) {
            addToPlayersWhoHaveHiddenLocalOOCChat(player);
        }
        if (args[0].equalsIgnoreCase("show")) {
            removeFromPlayersWhoHaveHiddenLocalOOCChat(player);
        }
        ArgumentParser argumentParser = new ArgumentParser();
        List<String> doubleQuoteArgs = argumentParser.getArgumentsInsideDoubleQuotes(args);
        if (doubleQuoteArgs.size() == 0) {
            player.sendMessage(colorChecker.getNegativeAlertColor() + "Message must be designated between double quotes.");
            return false;
        }
        String message = doubleQuoteArgs.get(0);
        String formatted = colorChecker.getColorByName(localOOCChatColor) + "" + String.format("<%s> (( %s ))", persistentData.getCharacter(player.getUniqueId()).getInfo("name"), message);
        messenger.sendOOCMessageToPlayersWithinDistance(player, formatted, localOOCChatRadius);
        return true;
    }

    private void addToPlayersWhoHaveHiddenLocalOOCChat(Player player) {
        if (!ephemeralData.getPlayersWhoHaveHiddenLocalOOCChat().contains(player.getUniqueId())) {
            ephemeralData.getPlayersWhoHaveHiddenLocalOOCChat().add(player.getUniqueId());
            player.sendMessage(colorChecker.getPositiveAlertColor() + "Local OOC Chat is now hidden!");
        }
        else {
            player.sendMessage(colorChecker.getNegativeAlertColor() + "Local OOC Chat is already hidden!");
        }
    }

    private void removeFromPlayersWhoHaveHiddenLocalOOCChat(Player player) {
        if (ephemeralData.getPlayersWhoHaveHiddenLocalOOCChat().contains(player.getUniqueId())) {
            ephemeralData.getPlayersWhoHaveHiddenLocalOOCChat().remove(player.getUniqueId());
            player.sendMessage(colorChecker.getPositiveAlertColor() + "Local OOC Chat is now visible!");
        }
        else {
            player.sendMessage(colorChecker.getNegativeAlertColor() + "Local OOC Chat is already visible!");
        }
    }
}