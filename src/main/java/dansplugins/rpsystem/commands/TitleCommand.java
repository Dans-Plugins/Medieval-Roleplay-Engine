package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.utils.ArgumentParser;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TitleCommand {

    public void titleBook(CommandSender sender, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            // check permission
            if (player.hasPermission("rp.title") || player.hasPermission("rp.default")) {

                // check if they're holding a book
                if (player.getInventory().getItemInMainHand().getType() == Material.WRITABLE_BOOK) {

                    // args check
                    if (args.length > 0) {

                        String newTitle = ArgumentParser.getInstance().createStringFromArgs(args);

                        ItemStack book = player.getInventory().getItemInMainHand();

                        ItemMeta meta = book.getItemMeta();

                        meta.setDisplayName(newTitle);

                        book.setItemMeta(meta);

                        player.getInventory().setItemInMainHand(book);

                        player.sendMessage(ColorChecker.getInstance().getPositiveAlertColor() + "Title added to book!");
                    }
                    else {
                        player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Usage: /title (new title)");
                    }

                }
                else {
                    player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "You have to be holding a book and quill to use this command!");
                }

            }
            else {
                player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.title'");
            }
        }

    }
}