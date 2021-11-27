package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.managers.ConfigManager;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.command.CommandSender;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

public class HelpCommand extends AbstractCommand {
    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("help"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("rp.help"));

    @Override
    public ArrayList<String> getNames() {
        return names;
    }

    @Override
    public ArrayList<String> getPermissions() {
        return permissions;
    }

    public boolean execute(CommandSender sender) {
        sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + " == Medieval Roleplay Engine " + MedievalRoleplayEngine.getInstance().getVersion() + " Commands == ");
        sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rphelp - Show a list of useful commands for the plugin.");
        sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/card help - Show a list of commands useful for managing character cards.");
        sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/bird - Send a bird to another player.");
        sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/roll or /dice - Roll a dice.");
        sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/title - Rename an unwritten book.");

        if (ConfigManager.getInstance().getBoolean("chatFeaturesEnabled")) {
            sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp - Enter local (RP) chat.");
            sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/ooc - Enter global (OOC) chat.");
            sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/emote or /me - Send an emote to nearby players.");
            sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/yell (message)- Send a single messages to far away players.");
            sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/whisper (message) - Send a single message to very close players.");
            sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/lo (message) - Send an single OOC message to nearby players.");
        }

        sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rpconfig - View and change config options.");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }

}
