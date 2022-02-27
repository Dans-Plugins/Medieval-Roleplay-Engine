package dansplugins.rpsystem.commands;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import dansplugins.rpsystem.data.EphemeralData;
import dansplugins.rpsystem.data.PersistentData;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

/**
 * @author Daniel McCoy Stephenson
 */
public class StatsCommand extends AbstractPluginCommand {

    public StatsCommand() {
        super(new ArrayList<>(Arrays.asList("stats")), new ArrayList<>(Arrays.asList("rp.stats")));
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.AQUA + "=== MRE Stats ===");
        commandSender.sendMessage(ChatColor.AQUA + "Number of character cards: " + PersistentData.getInstance().getCharacters().size());
        commandSender.sendMessage(ChatColor.AQUA + "Players in local chat: " + EphemeralData.getInstance().getPlayersSpeakingInLocalChat().size());
        commandSender.sendMessage(ChatColor.AQUA + "Players with busy birds: " + EphemeralData.getInstance().getPlayersWithBusyBirds().size());
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}