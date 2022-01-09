package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.objects.RPCharacter;
import dansplugins.rpsystem.services.LocalCharacterLookupService;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.spigot.tools.UUIDChecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 * @brief This class will allow a player to look up their character card as well as the cards of others.
 */
public class CardCommand extends AbstractPluginCommand {

    public CardCommand() {
        super(new ArrayList<>(Arrays.asList("card")), new ArrayList<>(Arrays.asList("rp.card")));
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return false;
        }
        Player player = (Player) commandSender;

        RPCharacter character = LocalCharacterLookupService.getInstance().lookup(player.getUniqueId());
        if (character == null) {
            commandSender.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "You don't have a character");
            return false;
        }

        character.sendCharacterInfo(commandSender);
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        UUIDChecker uuidChecker = new UUIDChecker();
        UUID targetUUID = uuidChecker.findUUIDBasedOnPlayerName(args[0]);
        if (targetUUID == null) {
            commandSender.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "That player wasn't found.");
            return false;
        }

        RPCharacter character = LocalCharacterLookupService.getInstance().lookup(targetUUID); // TODO: alter this to use character class instead

        if (character == null) {
            commandSender.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "That player doesn't have a card.");
            return false;
        }

        character.sendCharacterInfo(commandSender);
        return true;
    }
}