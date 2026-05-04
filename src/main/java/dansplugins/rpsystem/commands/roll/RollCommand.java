package dansplugins.rpsystem.commands.roll;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RollCommand {
    private static final int DEFAULT_DIE_SIZE = 20;
    private static final int DEFAULT_NUM_DICE = 1;
    private static final int MAX_DICE = 100;
    private static final int MAX_DIE_SIZE = 10000;
    private static final int MAX_MODIFIER = 10000;

    // Matches optional count 'd' sides optional modifier, e.g. "2d6+3", "d20", "1d8-2", "20"
    private static final Pattern DICE_NOTATION = Pattern.compile(
            "^(?:(\\d+)d(\\d+)|d(\\d+)|(\\d+))([+-]\\d+)?$",
            Pattern.CASE_INSENSITIVE
    );

    private final MedievalRoleplayEngine medievalRoleplayEngine;
    private final Random random = new Random();

    public RollCommand(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    public boolean rollDice(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (!(player.hasPermission("rp.roll") || player.hasPermission("rp.dice") || player.hasPermission("rp.default"))) {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need one of the following permissions: 'rp.roll', 'rp.dice', 'rp.default'");
            return true;
        }

        int numDice = DEFAULT_NUM_DICE;
        int dieSize = DEFAULT_DIE_SIZE;
        int modifier = 0;
        String notation = "1d" + DEFAULT_DIE_SIZE;

        if (args.length > 0) {
            String input = args[0];
            Matcher m = DICE_NOTATION.matcher(input);
            if (!m.matches()) {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "'" + input + "' is not valid dice notation. Use e.g. '20', 'd20', '2d6', or '3d8+3'.");
                return true;
            }

            try {
                if (m.group(1) != null) {
                    // NdM form
                    numDice = Integer.parseInt(m.group(1));
                    dieSize = Integer.parseInt(m.group(2));
                } else if (m.group(3) != null) {
                    // dM form (implicit 1 die)
                    numDice = 1;
                    dieSize = Integer.parseInt(m.group(3));
                } else {
                    // plain number — treat as die size (legacy behaviour)
                    numDice = 1;
                    dieSize = Integer.parseInt(m.group(4));
                }

                if (m.group(5) != null) {
                    modifier = Integer.parseInt(m.group(5));
                }
            } catch (NumberFormatException e) {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "'" + input + "' contains a value that is too large. Maximum: " + MAX_DICE + " dice, d" + MAX_DIE_SIZE + ", modifier ±" + MAX_MODIFIER + ".");
                return true;
            }

            if (numDice < 1 || dieSize < 1) {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "The number of dice and die size must each be at least 1.");
                return true;
            }

            if (numDice > MAX_DICE || dieSize > MAX_DIE_SIZE) {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Maximum allowed: " + MAX_DICE + " dice with up to a d" + MAX_DIE_SIZE + ".");
                return true;
            }

            if (modifier < -MAX_MODIFIER || modifier > MAX_MODIFIER) {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Modifier must be between -" + MAX_MODIFIER + " and +" + MAX_MODIFIER + ".");
                return true;
            }

            notation = buildNotation(numDice, dieSize, modifier);
        }

        int[] rolls = rollDice(numDice, dieSize);
        int sum = 0;
        for (int r : rolls) {
            sum += r;
        }
        int total = sum + modifier;

        String rollsDisplay = buildRollsDisplay(rolls);
        String modDisplay = modifier != 0 ? (modifier > 0 ? " +" + modifier : " " + modifier) : "";
        String message = medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "" + ChatColor.ITALIC
                + player.getName() + " rolled " + notation + ": "
                + rollsDisplay + modDisplay + " = " + total;

        player.sendMessage(message);
        medievalRoleplayEngine.messenger.sendRPMessageToPlayersWithinDistanceExcludingTarget(player, message, 25);
        return true;
    }

    private int[] rollDice(int numDice, int dieSize) {
        int[] results = new int[numDice];
        for (int i = 0; i < numDice; i++) {
            results[i] = random.nextInt(dieSize) + 1;
        }
        return results;
    }

    private String buildNotation(int numDice, int dieSize, int modifier) {
        String base = numDice + "d" + dieSize;
        if (modifier > 0) return base + "+" + modifier;
        if (modifier < 0) return base + modifier;
        return base;
    }

    private String buildRollsDisplay(int[] rolls) {
        if (rolls.length == 1) {
            return String.valueOf(rolls[0]);
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < rolls.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(rolls[i]);
        }
        sb.append("]");
        return sb.toString();
    }

}
