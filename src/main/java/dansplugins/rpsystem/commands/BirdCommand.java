package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.data.EphemeralData;
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

import static org.bukkit.Bukkit.getServer;

/**
 * @author Daniel McCoy Stephenson
 */
public class BirdCommand extends AbstractPluginCommand {
    private final ColorChecker colorChecker;
    private final EphemeralData ephemeralData;
    private final ConfigService configService;
    private final MedievalRoleplayEngine medievalRoleplayEngine;
    private final Messenger messenger;

    public BirdCommand(ColorChecker colorChecker, EphemeralData ephemeralData, ConfigService configService, MedievalRoleplayEngine medievalRoleplayEngine, Messenger messenger) {
        super(new ArrayList<>(Arrays.asList("bird")), new ArrayList<>(Arrays.asList("mre.bird")));
        this.colorChecker = colorChecker;
        this.ephemeralData = ephemeralData;
        this.configService = configService;
        this.medievalRoleplayEngine = medievalRoleplayEngine;
        this.messenger = messenger;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(colorChecker.getNegativeAlertColor() + "Usage: /bird (IGN) \"message\"");
        return false;
    }

    public boolean execute(CommandSender sender, String[] args) {
        // player check
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        if (ephemeralData.getPlayersWithBusyBirds().contains(player.getUniqueId())) {
            player.sendMessage(colorChecker.getNegativeAlertColor() + "Your bird is already on a mission!");
            return false;
        }

        if (args.length < 2) {
            return execute(sender);
        }

        ArgumentParser argumentParser = new ArgumentParser();
        List<String> doubleQuoteArgs = argumentParser.getArgumentsInsideDoubleQuotes(args);

        if (doubleQuoteArgs.size() == 0) {
            player.sendMessage(colorChecker.getNegativeAlertColor() + "Message must be designated between double quotes.");
            return false;
        }

        String message = doubleQuoteArgs.get(0);

        if (configService.getBoolean("preventSelfBirding") && args[0].equalsIgnoreCase(player.getName())) {
            player.sendMessage(ChatColor.RED + "You can't send a bird to yourself.");
            return false;
        }

        Player targetPlayer = getServer().getPlayer(args[0]);

        if (targetPlayer == null) {
            player.sendMessage(colorChecker.getNegativeAlertColor() + "That player isn't online!");
            return false;
        }

        if (!(player.getLocation().getWorld().getName().equalsIgnoreCase(targetPlayer.getLocation().getWorld().getName()))) {
            player.sendMessage(colorChecker.getNegativeAlertColor() + "You can't send a bird to a player in another world.");
            return false;
        }

        double distance = player.getLocation().distance(targetPlayer.getLocation());
        int blocksPerSecond = configService.getInt("birdSpeed");
        int seconds = (int)distance/blocksPerSecond;

        getServer().getScheduler().runTaskLater(medievalRoleplayEngine, new Runnable() {
            @Override
            public void run() {
                targetPlayer.sendMessage(colorChecker.getPositiveAlertColor() + "A bird lands nearby and drops a message at your feet! It was sent by " + player.getName() + ". It reads:\n");
                targetPlayer.sendMessage(colorChecker.getPositiveAlertColor() + "" + ChatColor.ITALIC + "'" + message + "'");
                player.sendMessage(colorChecker.getPositiveAlertColor() + "Your bird has reached " + targetPlayer.getName() + "!");
                ephemeralData.getPlayersWithBusyBirds().remove(player.getUniqueId());
                messenger.sendRPMessageToPlayersWithinDistanceExcludingTarget(targetPlayer, colorChecker.getNeutralAlertColor() + String.format("A bird lands nearby and drops a message at the feet of %s!", targetPlayer.getName()), 10);
            }
        }, seconds * 20L);

        player.sendMessage(colorChecker.getPositiveAlertColor() + "The bird flies off with your message.");
        ephemeralData.getPlayersWithBusyBirds().add(player.getUniqueId());
        return true;
    }
}