package dansplugins.rpsystem.commands.title;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TitleCommand {
    private final MedievalRoleplayEngine medievalRoleplayEngine;

    public TitleCommand(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    public void titleBook(CommandSender sender, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            // check permission
            if (player.hasPermission("rp.title") || player.hasPermission("rp.default")) {

                // check if they're holding a book
                if (player.getInventory().getItemInMainHand().getType() == Material.WRITABLE_BOOK) {

                    // args check
                    if (args.length > 0) {

                        String newTitle = medievalRoleplayEngine.argumentParser.createStringFromArgs(args);

                        ItemStack book = player.getInventory().getItemInMainHand();

                        ItemMeta meta = book.getItemMeta();

                        meta.setDisplayName(newTitle);

                        book.setItemMeta(meta);

                        player.getInventory().setItemInMainHand(book);

                        player.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor() + "Title added to book!");
                    }
                    else {
                        player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Usage: /title (new title)");
                    }

                }
                else {
                    player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "You have to be holding a book and quill to use this command!");
                }

            }
            else {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.title'");
            }
        }

    }
}