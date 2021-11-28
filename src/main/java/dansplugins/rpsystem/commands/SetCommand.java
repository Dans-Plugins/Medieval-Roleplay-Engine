package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.objects.RPCharacter;
import dansplugins.rpsystem.services.CharacterLookupService;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This class will allow players to set various fields in their cards.
 */
public class SetCommand extends AbstractCommand {
    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("set"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("rp.set"));

    @Override
    public ArrayList<String> getNames() {
        return names;
    }

    @Override
    public ArrayList<String> getPermissions() {
        return permissions;
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

        RPCharacter character = CharacterLookupService.getInstance().lookup(player.getUniqueId());
        if (character == null) {
            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "You don't have a character.");
            return false;
        }

        ArrayList<String> doubleQuoteArgs = MedievalRoleplayEngine.getInstance().getToolbox().getArgumentParser().getArgumentsInsideDoubleQuotes(args);

        if (doubleQuoteArgs.size() < 2) {
            player.sendMessage(ChatColor.RED + "Key and value must be designated within double quotes.");
            return false;
        }

        String key = doubleQuoteArgs.get(0);
        String value = doubleQuoteArgs.get(0);

        character.setInfo(key, value);
        player.sendMessage(ChatColor.GREEN + "Your character has been updated.");
        return true;
    }
}
