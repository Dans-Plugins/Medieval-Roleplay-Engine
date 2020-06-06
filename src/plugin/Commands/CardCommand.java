package plugin.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugin.CharacterCard;

import java.util.ArrayList;

public class CardCommand {

    public static void showCard(CommandSender sender, String[] args, ArrayList<CharacterCard> cards) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            for (CharacterCard card : cards) {

                if (card.getPlayerName().equalsIgnoreCase(player.getName())) {

                    player.sendMessage("Name: " + card.getName());
                    player.sendMessage("Race: " + card.getRace());
                    player.sendMessage("Subculture: " + card.getSubculture());
                    player.sendMessage("Age: " + card.getAge());
                    player.sendMessage("Gender: " + card.getGender());
                    player.sendMessage("Religion: " + card.getReligion());
                }
            }
        }
    }
}
