package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.objects.RPCharacter;
import dansplugins.rpsystem.services.CharacterLookupService;
import dansplugins.rpsystem.utils.ColorChecker;
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
public class UnsetCommand extends AbstractPluginCommand {
    private final CharacterLookupService characterLookupService;
    private final ColorChecker colorChecker;

    public UnsetCommand(CharacterLookupService characterLookupService, ColorChecker colorChecker) {
        super(new ArrayList<>(Arrays.asList("unset")), new ArrayList<>(Arrays.asList("rp.unset")));
        this.characterLookupService = characterLookupService;
        this.colorChecker = colorChecker;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.RED + "Usage: /rp unset \"key\"");
        return false;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return false;
        }
        Player player = (Player) commandSender;

        RPCharacter character = characterLookupService.lookup(player.getUniqueId());
        if (character == null) {
            player.sendMessage(colorChecker.getNegativeAlertColor() + "You don't have a character.");
            return false;
        }

        ArgumentParser argumentParser = new ArgumentParser();
        List<String> doubleQuoteArgs = argumentParser.getArgumentsInsideDoubleQuotes(args);

        if (doubleQuoteArgs.size() < 1) {
            player.sendMessage(ChatColor.RED + "Key must be designated within double quotes.");
            return false;
        }

        String key = doubleQuoteArgs.get(0);

        if (character.getInfo(key) == null) {
            player.sendMessage(ChatColor.RED + "That field doesn't exist.");
            return false;
        }

        character.unsetInfo(key);
        player.sendMessage(ChatColor.GREEN + "That field has been unset.");
        return true;
    }
}