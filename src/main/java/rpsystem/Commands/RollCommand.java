package rpsystem.Commands;

import com.bernardomg.tabletop.dice.history.RollHistory;
import com.bernardomg.tabletop.dice.interpreter.DiceInterpreter;
import com.bernardomg.tabletop.dice.interpreter.DiceRoller;
import com.bernardomg.tabletop.dice.parser.DefaultDiceParser;
import com.bernardomg.tabletop.dice.parser.DiceParser;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rpsystem.Main;

import java.util.regex.Pattern;

public class RollCommand {

    Main main = null;

    static final DiceParser parser = new DefaultDiceParser();
    static final DiceInterpreter<RollHistory> roller = new DiceRoller();

    static final String usageMsg = ChatColor.RED + "Usage: /roll (dice-count)d(side-count)+(modifier)";
    static final String successMsg = ChatColor.GREEN + "You rolled a %s with a total value of %d";
    public static final String invalidSyntaxMsg = ChatColor.RED + "Sorry! Invalid arguments, must be in standard Dice Notation (2d6+12)";
    public static final String noPermMsg = ChatColor.RED + "Sorry! In order to use this command, you need the following permission: 'rp.roll'";

    public RollCommand(Main plugin) {
        main = plugin;
    }

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

            final RollHistory rolls = parser.parse(args[0], roller);
            player.sendMessage(String.format(successMsg, rolls.getRollResults().toString(), rolls.getTotalRoll().toString()));

        } else {
            player.sendMessage(noPermMsg);
        }
    }
}
