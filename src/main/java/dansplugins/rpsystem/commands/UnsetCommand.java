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

public class UnsetCommand extends AbstractCommand {
    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("unset"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("rp.unset"));

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

        RPCharacter character = CharacterLookupService.getInstance().lookup(player.getUniqueId());
        if (character == null) {
            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "You don't have a character.");
            return false;
        }

        ArrayList<String> doubleQuoteArgs = MedievalRoleplayEngine.getInstance().getToolbox().getArgumentParser().getArgumentsInsideDoubleQuotes(args);

        if (doubleQuoteArgs.size() < 1) {
            player.sendMessage(ChatColor.RED + "Key must be designated within double quotes.");
            return false;
        }

        String key = doubleQuoteArgs.get(0);
        character.unsetInfo(key);
        player.sendMessage(ChatColor.GREEN + "That field has been unset.");
        return true;
    }
}