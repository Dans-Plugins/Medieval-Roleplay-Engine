package dansplugins.rpsystem.commands;

import static org.bukkit.Bukkit.getServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dansplugins.mailboxes.utils.UUIDChecker;
import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.data.EphemeralData;
import dansplugins.rpsystem.integrators.MailboxesIntegrator;
import dansplugins.rpsystem.services.LocalConfigService;
import dansplugins.rpsystem.utils.ColorChecker;
import dansplugins.rpsystem.utils.Messenger;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.misc.ArgumentParser;

/**
 * @author Daniel McCoy Stephenson
 */
public class BirdCommand extends AbstractPluginCommand {

    public BirdCommand() {
        super(new ArrayList<>(Arrays.asList("bird")), new ArrayList<>(Arrays.asList("rp.bird")));
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Usage: /bird (IGN) \"message\"");
        return false;
    }

    public boolean execute(CommandSender sender, String[] args) {
        // player check
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        if (EphemeralData.getInstance().getPlayersWithBusyBirds().contains(player.getUniqueId())) {
            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Your bird is already on a mission!");
            return false;
        }

        if (args.length < 2) {
            return execute(sender);
        }

        ArgumentParser argumentParser = new ArgumentParser();
        ArrayList<String> doubleQuoteArgs = argumentParser.getArgumentsInsideDoubleQuotes(args);

        if (doubleQuoteArgs.size() == 0) {
            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Message must be designated between double quotes.");
            return false;
        }

        String message = doubleQuoteArgs.get(0);

        if (LocalConfigService.getInstance().getBoolean("preventSelfBirding") && args[0].equalsIgnoreCase(player.getName())) {
            player.sendMessage(ChatColor.RED + "You can't send a bird to yourself.");
            return false;
        }

        Player targetPlayer = getServer().getPlayer(args[0]);

        if (targetPlayer == null) {
            if (attemptToSendMessageToPlayersMailbox(args[0], player, message)) {
                return true;
            }
            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "That player isn't online!");
            return false;
        }

        if (!(player.getLocation().getWorld().getName().equalsIgnoreCase(targetPlayer.getLocation().getWorld().getName()))) {
            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "You can't send a bird to a player in another world.");
            return false;
        }

        double distance = player.getLocation().distance(targetPlayer.getLocation());
        int blocksPerSecond = 20;
        int seconds = (int)distance/blocksPerSecond;

        getServer().getScheduler().runTaskLater(MedievalRoleplayEngine.getInstance(), new Runnable() {
            @Override
            public void run() {
                targetPlayer.sendMessage(ColorChecker.getInstance().getPositiveAlertColor() + "A bird lands nearby and drops a message at your feet! It was sent by " + player.getName() + ". It reads:\n");
                targetPlayer.sendMessage(ColorChecker.getInstance().getPositiveAlertColor() + "" + ChatColor.ITALIC + "'" + message + "'");
                player.sendMessage(ColorChecker.getInstance().getPositiveAlertColor() + "Your bird has reached " + targetPlayer.getName() + "!");
                EphemeralData.getInstance().getPlayersWithBusyBirds().remove(player.getUniqueId());
                Messenger.getInstance().sendRPMessageToPlayersWithinDistanceExcludingTarget(targetPlayer, ColorChecker.getInstance().getNeutralAlertColor() + String.format("A bird lands nearby and drops a message at the feet of %s!", targetPlayer.getName()), 10);
            }
        }, seconds * 20);

        player.sendMessage(ColorChecker.getInstance().getPositiveAlertColor() + "The bird flies off with your message.");
        EphemeralData.getInstance().getPlayersWithBusyBirds().add(player.getUniqueId());
        return true;
    }

    boolean attemptToSendMessageToPlayersMailbox(String targetName, Player sender, String message) {
        if (MailboxesIntegrator.getInstance().isMailboxesPresent()) {
            UUIDChecker uuidChecker = new UUIDChecker();
            UUID targetUUID = uuidChecker.findUUIDBasedOnPlayerName(targetName);
            if (targetUUID != null) {
                String messageToSend = "While you were offline, a bird dropped off a message for you. It was sent by " + sender.getName() + ". It reads:\n\n'" + message + "'";

                boolean success = MailboxesIntegrator.getInstance().getAPI().sendPluginMessageToPlayer(MedievalRoleplayEngine.getInstance().getName(), targetUUID, messageToSend);
                if (success) {
                    sender.sendMessage(ColorChecker.getInstance().getPositiveAlertColor() + "The bird flies off with your message. Since this player is offline, this message will go to their mailbox.");
                    return true;
                }
                else {
                    sender.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Your bird wasn't able to find this player's mailbox.");
                    return false;
                }
            }
        }
        return false;
    }
}