package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.Messenger;
import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.utils.ArgumentParser;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

public class EmoteCommand extends AbstractCommand {
    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("emote"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("rp.emote"));

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
        // TODO: implement
        return false;
    }

    public boolean execute(CommandSender sender, String[] args) {

        int emoteRadius = MedievalRoleplayEngine.getInstance().getConfig().getInt("emoteRadius");
        String emoteColor = MedievalRoleplayEngine.getInstance().getConfig().getString("emoteColor");

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 0) {
                String message = ArgumentParser.getInstance().createStringFromFirstArgOnwards(args, 0);
                String characterName = PersistentData.getInstance().getCard(player.getUniqueId()).getName();

                Messenger.getInstance().sendRPMessageToPlayersWithinDistance(player, ColorChecker.getInstance().getColorByName(emoteColor) + "" + ChatColor.ITALIC + characterName + " " + message, emoteRadius);
            }

        }
        return false;
    }

}
