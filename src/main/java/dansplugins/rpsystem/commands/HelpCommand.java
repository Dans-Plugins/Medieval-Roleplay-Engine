package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.services.LocalConfigService;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class HelpCommand extends AbstractPluginCommand {

    public HelpCommand() {
        super(new ArrayList<>(Arrays.asList("help")), new ArrayList<>(Arrays.asList("rp.help")));
    }

    public boolean execute(CommandSender sender) {
        sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + " == Medieval Roleplay Engine " + MedievalRoleplayEngine.getInstance().getVersion() + " Commands == ");
        sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp help - Show a list of useful commands for the plugin.");
        sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp card <IGN> - View your character card or other players' character cards.");
        sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp set \"key\" \"value\" - Set fields in your character card.");
        sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp unset \"key\" - Unset a field in your character card.");
        sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp bird (IGN) \"message\" - Send a bird to another player.");
        sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp roll (number) - Roll a dice.");
        sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp title \"new title\" - Rename an unwritten book.");

        if (LocalConfigService.getInstance().getBoolean("chatFeaturesEnabled")) {
            sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp local - Enter local (RP) chat.");
            sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp global - Enter global (OOC) chat.");
            sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp emote - Send an emote to nearby players.");
            sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp yell \"message\"- Send a single RP message to far away players.");
            sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp whisper \"message\" - Send a single RP message to very close players.");
            sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp lo \"message\" - Send an single OOC message to nearby players.");
        }

        sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp config - View and change config options.");
        sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp force - Force actions to occur.");
        sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/rp stats - View various statistics associated with the plugin.");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}