package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.data.EphemeralData;
import dansplugins.rpsystem.data.PersistentData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class StatsCommand extends AbstractPluginCommand {
    private final PersistentData persistentData;
    private final EphemeralData ephemeralData;

    public StatsCommand(PersistentData persistentData, EphemeralData ephemeralData) {
        super(new ArrayList<>(Arrays.asList("stats")), new ArrayList<>(Arrays.asList("mre.stats")));
        this.persistentData = persistentData;
        this.ephemeralData = ephemeralData;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.AQUA + "=== MRE Stats ===");
        commandSender.sendMessage(ChatColor.AQUA + "Number of character cards: " + persistentData.getCharacters().size());
        commandSender.sendMessage(ChatColor.AQUA + "Players in local chat: " + ephemeralData.getPlayersSpeakingInLocalChat().size());
        commandSender.sendMessage(ChatColor.AQUA + "Players with busy birds: " + ephemeralData.getPlayersWithBusyBirds().size());
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}