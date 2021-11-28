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
import java.util.UUID;

/**
 * This class will allow a player to look up their character card as well as the cards of others.
 */
public class CardCommand extends AbstractCommand {
    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("card"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("rp.card"));

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
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return false;
        }
        Player player = (Player) commandSender;

        RPCharacter character = CharacterLookupService.getInstance().lookup(player.getUniqueId());
        if (character == null) {
            commandSender.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "You don't have a card.");
            return false;
        }

        character.sendCharacterInfo(commandSender);
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {

        // get UUID
        UUID targetUUID = MedievalRoleplayEngine.getInstance().getToolbox().getUUIDChecker().findUUIDBasedOnPlayerName(args[0]);
        if (targetUUID == null) {
            commandSender.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "That player wasn't found.");
            return false;
        }

        RPCharacter character = CharacterLookupService.getInstance().lookup(targetUUID); // TODO: alter this to use character class instead

        if (character == null) {
            commandSender.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "That player doesn't have a card.");
            return false;
        }

        character.sendCharacterInfo(commandSender);
        return true;
    }
}