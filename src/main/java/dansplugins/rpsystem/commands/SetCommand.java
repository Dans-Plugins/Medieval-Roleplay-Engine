package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.objects.RPCharacter;
import dansplugins.rpsystem.services.LocalCharacterLookupService;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.abs.AbstractPluginCommand;
import preponderous.ponder.misc.ArgumentParser;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 * @brief This class will allow players to set various fields in their cards.
 */
public class SetCommand extends AbstractPluginCommand {

    public SetCommand() {
        super(new ArrayList<>(Arrays.asList("set")), new ArrayList<>(Arrays.asList("rp.set")));
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.RED + "Usage: /rp set \"key\" \"value\"");
        return false;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return false;
        }
        Player player = (Player) commandSender;

        RPCharacter character = LocalCharacterLookupService.getInstance().lookup(player.getUniqueId());
        if (character == null) {
            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "You don't have a character.");
            return false;
        }

        ArgumentParser argumentParser = new ArgumentParser();
        ArrayList<String> doubleQuoteArgs = argumentParser.getArgumentsInsideDoubleQuotes(args);

        if (doubleQuoteArgs.size() < 2) {
            player.sendMessage(ChatColor.RED + "Key and value must be designated within double quotes.");
            return false;
        }

        String key = doubleQuoteArgs.get(0);
        String value = doubleQuoteArgs.get(1);

        character.setInfo(key, value);
        player.sendMessage(ChatColor.GREEN + "Your character has been updated.");
        return true;
    }
}
