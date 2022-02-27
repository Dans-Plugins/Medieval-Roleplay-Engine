package dansplugins.rpsystem.commands;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dansplugins.rpsystem.utils.ColorChecker;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.misc.ArgumentParser;

/**
 * @author Daniel McCoy Stephenson
 */
public class TitleCommand extends AbstractPluginCommand {

    public TitleCommand() {
        super(new ArrayList<>(Arrays.asList("title")), new ArrayList<>(Arrays.asList("rp.title")));
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Usage: /title (new title)");
        return false;
    }

    public boolean execute(CommandSender sender, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            // check if they're holding a book
            if (player.getInventory().getItemInMainHand().getType() == Material.WRITABLE_BOOK) {

                if (args.length == 0) {
                    return execute(sender);
                }

                ArgumentParser argumentParser = new ArgumentParser();
                ArrayList<String> doubleQuoteArgs = argumentParser.getArgumentsInsideDoubleQuotes(args);

                if (doubleQuoteArgs.size() == 0) {
                    player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "New title must be designated between double quotes.");
                    return false;
                }

                String newTitle = doubleQuoteArgs.get(0);

                ItemStack book = player.getInventory().getItemInMainHand();

                ItemMeta meta = book.getItemMeta();

                meta.setDisplayName(newTitle);

                book.setItemMeta(meta);

                player.getInventory().setItemInMainHand(book);

                player.sendMessage(ColorChecker.getInstance().getPositiveAlertColor() + "Title added to book!");

            }
            else {
                player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "You have to be holding a book and quill to use this command!");
            }
        }
        return true;
    }
}