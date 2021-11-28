package dansplugins.rpsystem.commands.deprecated;

import com.bernardomg.tabletop.dice.history.RollHistory;
import com.bernardomg.tabletop.dice.interpreter.DiceInterpreter;
import com.bernardomg.tabletop.dice.interpreter.DiceRoller;
import com.bernardomg.tabletop.dice.parser.DefaultDiceParser;
import com.bernardomg.tabletop.dice.parser.DiceParser;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Deprecated
public class NewRollCommand {

    static final DiceParser parser = new DefaultDiceParser();
    static final DiceInterpreter<RollHistory> roller = new DiceRoller();

    static final String usageMsg = ColorChecker.getInstance().getNegativeAlertColor() + "Usage: /roll (dice-count)d(side-count)+(modifier)";
    public static final String invalidSyntaxMsg = ColorChecker.getInstance().getNegativeAlertColor() + "Sorry! Invalid arguments, must be in standard Dice Notation (2d6+12)";
    public static final String noPermMsg = ColorChecker.getInstance().getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.roll'";

    public static void rollDice(CommandSender sender, String[] args) {
        // player check
        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        if (player.hasPermission("rp.roll") || player.hasPermission("rp.default")) {

            // zero args check
            if (args.length < 1) {
                player.sendMessage(usageMsg);
                return;
            }

            // Invalid syntax check
            if (!Pattern.matches("^(\\d+)?d(\\d+)([+-]\\d+)?$", args[0])) {
                player.sendMessage(invalidSyntaxMsg);
                return;
            }

            player.sendMessage(processRolls(parser.parse(args[0], roller)));
        } else {
            player.sendMessage(noPermMsg);
        }
    }

    private static String processRolls(RollHistory rolls) {
        StringBuilder messageBuilder = new StringBuilder(ColorChecker.getInstance().getPositiveAlertColor() + "You rolled a ");

        rolls.getRollResults().forEach(rollResult -> {

            List<String> results = new ArrayList<>();

            rollResult.getAllRolls().forEach(roll -> results.add(String.valueOf(roll)));

            messageBuilder.append(String.join(", ", results));

            if (results.size() > 1) {
                messageBuilder.append(", with a total value of ").append(rollResult.getTotalRoll());
            }

            messageBuilder.append(".");
        });

        return messageBuilder.toString();
    }
}
