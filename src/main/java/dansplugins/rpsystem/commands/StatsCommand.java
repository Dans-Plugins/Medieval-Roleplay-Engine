package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.data.EphemeralData;
import dansplugins.rpsystem.data.PersistentData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;

public class StatsCommand extends AbstractCommand {
    @Override
    public ArrayList<String> getNames() {
        return super.getNames();
    }

    @Override
    public ArrayList<String> getPermissions() {
        return super.getPermissions();
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.AQUA + "=== MRE Stats ===");
        commandSender.sendMessage(ChatColor.AQUA + "Number of character cards: " + PersistentData.getInstance().getCards().size());
        commandSender.sendMessage(ChatColor.AQUA + "Players in local chat: " + EphemeralData.getInstance().getPlayersSpeakingInLocalChat().size());
        commandSender.sendMessage(ChatColor.AQUA + "Players with busy birds: " + EphemeralData.getInstance().getPlayersWithBusyBirds().size());
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}
