package rpsystem.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rpsystem.CharacterCard;

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

    public static void changeName(CommandSender sender, String[] args, ArrayList<CharacterCard> cards) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            for (CharacterCard card : cards) {

                if (card.getPlayerName().equalsIgnoreCase(player.getName())) {

                    if (args.length > 1) {
                        card.setName(args[1]);
                        player.sendMessage("Name set! Type /card to see changes.");
                    }

                }
            }
        }
    }

    public static void changeRace(CommandSender sender, String[] args, ArrayList<CharacterCard> cards) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            for (CharacterCard card : cards) {

                if (card.getPlayerName().equalsIgnoreCase(player.getName())) {

                    if (args.length > 1) {
                        card.setRace(args[1]);
                        player.sendMessage("Race set! Type /card to see changes.");
                    }

                }
            }
        }
    }

    public static void changeSubculture(CommandSender sender, String[] args, ArrayList<CharacterCard> cards) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            for (CharacterCard card : cards) {

                if (card.getPlayerName().equalsIgnoreCase(player.getName())) {

                    if (args.length > 1) {
                        card.setSubculture(args[1]);
                        player.sendMessage("Subculture set! Type /card to see changes.");
                    }

                }
            }
        }
    }

    public static void changeReligion(CommandSender sender, String[] args, ArrayList<CharacterCard> cards) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            for (CharacterCard card : cards) {

                if (card.getPlayerName().equalsIgnoreCase(player.getName())) {

                    if (args.length > 1) {
                        card.setReligion(args[1]);
                        player.sendMessage("Religion set! Type /card to see changes.");
                    }

                }
            }
        }
    }

    public static void changeAge(CommandSender sender, String[] args, ArrayList<CharacterCard> cards) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            for (CharacterCard card : cards) {

                if (card.getPlayerName().equalsIgnoreCase(player.getName())) {

                    if (args.length > 1) {
                        card.setAge(Integer.parseInt(args[1]));
                        player.sendMessage("Age set! Type /card to see changes.");
                    }

                }
            }
        }
    }

    public static void changeGender(CommandSender sender, String[] args, ArrayList<CharacterCard> cards) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            for (CharacterCard card : cards) {

                if (card.getPlayerName().equalsIgnoreCase(player.getName())) {

                    if (args.length > 1) {
                        card.setGender(args[1]);
                        player.sendMessage("Gender set! Type /card to see changes.");
                    }

                }
            }
        }
    }

    public static void showPlayerInfo(CommandSender sender, String[] args, ArrayList<CharacterCard> cards) {
        for (CharacterCard card : cards) {
            if (args.length > 0) {
                if (card.getPlayerName().equals(args[0])) {
                    sender.sendMessage("Name: " + card.getName());
                    sender.sendMessage("Race: " + card.getRace());
                    sender.sendMessage("Subculture: " + card.getSubculture());
                    sender.sendMessage("Age: " + card.getAge());
                    sender.sendMessage("Gender: " + card.getGender());
                    sender.sendMessage("Religion: " + card.getReligion());
                    return;
                }
            }
        }
    }

}
