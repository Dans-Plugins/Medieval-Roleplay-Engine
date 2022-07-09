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
        super(new ArrayList<>(Arrays.asList("help")), new ArrayList<>(Arrays.asList("mre.help")));
        this.colorChecker = colorChecker;
        this.medievalRoleplayEngine = medievalRoleplayEngine;
        this.configService = configService;
    }

    public boolean execute(CommandSender sender) {
        sender.sendMessage(colorChecker.getNeutralAlertColor() + " == Medieval Roleplay Engine " + medievalRoleplayEngine.getVersion() + " Commands == ");
        sender.sendMessage(colorChecker.getNeutralAlertColor() + "/mre help - Show a list of useful commands for the plugin.");
        sender.sendMessage(colorChecker.getNeutralAlertColor() + "/mre card <IGN> - View your character card or other players' character cards.");
        sender.sendMessage(colorChecker.getNeutralAlertColor() + "/mre set \"key\" \"value\" - Set fields in your character card.");
        if (sender.hasPermission("mre.set.others")) {
            sender.sendMessage(colorChecker.getNeutralAlertColor() + "/mre set \"key\" \"ign\" - Set a field in another players' character card.");
        }
        sender.sendMessage(colorChecker.getNeutralAlertColor() + "/mre unset \"key\" - Unset a field in your character card.");
        if (sender.hasPermission("mre.unset.others")) {
            sender.sendMessage(colorChecker.getNeutralAlertColor() + "/mre unset \"key\" \"ign\" - Unset a field in another players' character card.");
        }
        sender.sendMessage(colorChecker.getNeutralAlertColor() + "/mre bird (IGN) \"message\" - Send a bird to another player.");
        sender.sendMessage(colorChecker.getNeutralAlertColor() + "/mre roll (number) - Roll a dice.");
        sender.sendMessage(colorChecker.getNeutralAlertColor() + "/mre title \"new title\" - Rename an unwritten book.");

        if (configService.getBoolean("chatFeaturesEnabled")) {
            sender.sendMessage(colorChecker.getNeutralAlertColor() + "/mre local - Enter local (RP) chat.");
            sender.sendMessage(colorChecker.getNeutralAlertColor() + "/mre global - Enter global (OOC) chat.");
            sender.sendMessage(colorChecker.getNeutralAlertColor() + "/mre emote - Send an emote to nearby players.");
            sender.sendMessage(colorChecker.getNeutralAlertColor() + "/mre yell \"message\"- Send a single RP message to far away players.");
            sender.sendMessage(colorChecker.getNeutralAlertColor() + "/mre whisper \"message\" - Send a single RP message to very close players.");
            sender.sendMessage(colorChecker.getNeutralAlertColor() + "/mre lo \"message\" - Send an single OOC message to nearby players.");
        }

        sender.sendMessage(colorChecker.getNeutralAlertColor() + "/mre stats - View various statistics associated with the plugin.");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}