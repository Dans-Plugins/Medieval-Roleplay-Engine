package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

public class TitleCommand extends AbstractCommand {
    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("title"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("rp.title"));

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

                ArrayList<String> doubleQuoteArgs = MedievalRoleplayEngine.getInstance().getToolbox().getArgumentParser().getArgumentsInsideDoubleQuotes(args);

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