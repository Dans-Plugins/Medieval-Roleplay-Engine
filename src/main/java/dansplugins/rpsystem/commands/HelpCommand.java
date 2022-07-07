package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.services.ConfigService;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class HelpCommand extends AbstractPluginCommand {
    private final ColorChecker colorChecker;
    private final MedievalRoleplayEngine medievalRoleplayEngine;
    private final ConfigService configService;

    public HelpCommand(ColorChecker colorChecker, MedievalRoleplayEngine medievalRoleplayEngine, ConfigService configService) {
        super(new ArrayList<>(Arrays.asList("help")), new ArrayList<>(Arrays.asList("rp.help")));
        this.colorChecker = colorChecker;
        this.medievalRoleplayEngine = medievalRoleplayEngine;
        this.configService = configService;
    }

    public boolean execute(CommandSender sender) {
        sender.sendMessage(colorChecker.getNeutralAlertColor() + " == Medieval Roleplay Engine " + medievalRoleplayEngine.getVersion() + " Commands == ");
        sender.sendMessage(colorChecker.getNeutralAlertColor() + "/rp help - Show a list of useful commands for the plugin.");
        sender.sendMessage(colorChecker.getNeutralAlertColor() + "/rp card <IGN> - View your character card or other players' character cards.");
        sender.sendMessage(colorChecker.getNeutralAlertColor() + "/rp set \"key\" \"value\" - Set fields in your character card.");
        sender.sendMessage(colorChecker.getNeutralAlertColor() + "/rp unset \"key\" - Unset a field in your character card.");
        sender.sendMessage(colorChecker.getNeutralAlertColor() + "/rp bird (IGN) \"message\" - Send a bird to another player.");
        sender.sendMessage(colorChecker.getNeutralAlertColor() + "/rp roll (number) - Roll a dice.");
        sender.sendMessage(colorChecker.getNeutralAlertColor() + "/rp title \"new title\" - Rename an unwritten book.");

        if (configService.getBoolean("chatFeaturesEnabled")) {
            sender.sendMessage(colorChecker.getNeutralAlertColor() + "/rp local - Enter local (RP) chat.");
            sender.sendMessage(colorChecker.getNeutralAlertColor() + "/rp global - Enter global (OOC) chat.");
            sender.sendMessage(colorChecker.getNeutralAlertColor() + "/rp emote - Send an emote to nearby players.");
            sender.sendMessage(colorChecker.getNeutralAlertColor() + "/rp yell \"message\"- Send a single RP message to far away players.");
            sender.sendMessage(colorChecker.getNeutralAlertColor() + "/rp whisper \"message\" - Send a single RP message to very close players.");
            sender.sendMessage(colorChecker.getNeutralAlertColor() + "/rp lo \"message\" - Send an single OOC message to nearby players.");
        }

        sender.sendMessage(colorChecker.getNeutralAlertColor() + "/rp stats - View various statistics associated with the plugin.");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}