package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.MedievalRoleplayEngine;
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
        sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp help - Show a list of useful commands for the plugin.");
        sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp card");
        sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp bird - Send a bird to another player.");
        sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp roll or /dice - Roll a dice.");
        sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp title - Rename an unwritten book.");

        if (MedievalRoleplayEngine.getInstance().getPonderAPI().getConfigService().getBoolean("chatFeaturesEnabled")) {
            sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp local - Enter local (RP) chat.");
            sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp global - Enter global (OOC) chat.");
            sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp emote or /me - Send an emote to nearby players.");
            sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp yell (message)- Send a single messages to far away players.");
            sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp whisper (message) - Send a single message to very close players.");
            sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp lo (message) - Send an single OOC message to nearby players.");
        }

        sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp config - View and change config options.");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }

}
