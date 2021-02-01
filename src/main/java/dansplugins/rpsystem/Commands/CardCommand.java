package dansplugins.rpsystem.Commands;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.Objects.CharacterCard;
import dansplugins.rpsystem.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static org.bukkit.Bukkit.getServer;

public class CardCommand {

    public static void showCard(CommandSender sender, String[] args, ArrayList<CharacterCard> cards) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.show") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : cards) {
                    if (card.getPlayerUUID() != null) {
                        if (card.getPlayerUUID().equals(player.getUniqueId())) {
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.AQUA + "\n----------\n" + "Character Card of " + Bukkit.getOfflinePlayer(card.getPlayerUUID()).getName() + "\n----------\n");
                            player.sendMessage(ChatColor.AQUA + "Name: " + card.getName());
                            player.sendMessage(ChatColor.AQUA + "Race: " + card.getRace());
                            player.sendMessage(ChatColor.AQUA + "Subculture: " + card.getSubculture());
                            player.sendMessage(ChatColor.AQUA + "Age: " + card.getAge());
                            player.sendMessage(ChatColor.AQUA + "Gender: " + card.getGender());
                            player.sendMessage(ChatColor.AQUA + "Religion: " + card.getReligion());
                            return;
                        }
                    }
                }
            }
            else {
                player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need the following permission: 'rp.card.show'");
            }

        }
    }

    public static void showHelpMessage(CommandSender sender) {
        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (player.hasPermission("rp.card.help") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                sender.sendMessage(ChatColor.BOLD + "" + ChatColor.AQUA + " == " + "Character Card Commands" + " == ");
                sender.sendMessage(ChatColor.AQUA + "/card - View your character card.");
                sender.sendMessage(ChatColor.AQUA + "/card (player) - View the character card of a specific player.");
                sender.sendMessage(ChatColor.AQUA + "/card name (name) - Change your character's name.");
                sender.sendMessage(ChatColor.AQUA + "/card race (race) - Change your character's race.");
                sender.sendMessage(ChatColor.AQUA + "/card subculture (subculture) - Change your character's subculture.");
                sender.sendMessage(ChatColor.AQUA + "/card age (age) - Change your character's age.");
                sender.sendMessage(ChatColor.AQUA + "/card gender (gender) - Change your character's gender.");
                sender.sendMessage(ChatColor.AQUA + "/card religion (religion) - Change your character's religion.");
            }
            else {
                player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need the following permission: 'rp.card.help'");
            }
        }

    }

    public void changeName(CommandSender sender, String[] args, ArrayList<CharacterCard> cards) {

        int changeNameCooldown = MedievalRoleplayEngine.getInstance().getConfig().getInt("changeNameCooldown");

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.name") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : cards) {

                        if (card.getPlayerUUID().equals(player.getUniqueId())) {

                            if (!MedievalRoleplayEngine.getInstance().playersOnNameChangeCooldown.contains(player.getUniqueId())) {

                                if (args.length > 1) {
                                    card.setName(Utilities.createStringFromFirstArgOnwards(args, 1));
                                    player.sendMessage(ChatColor.GREEN + "Name set! Type /card to see changes.");

                                    if (changeNameCooldown != 0) {
                                        // cooldown
                                        MedievalRoleplayEngine.getInstance().playersOnNameChangeCooldown.add(player.getUniqueId());

                                        int seconds = changeNameCooldown;
                                        getServer().getScheduler().runTaskLater(MedievalRoleplayEngine.getInstance(), new Runnable() {
                                            @Override
                                            public void run() {
                                                MedievalRoleplayEngine.getInstance().playersOnNameChangeCooldown.remove(player.getUniqueId());
                                                player.sendMessage(ChatColor.GREEN + "You can now change your character's name again.");
                                            }
                                        }, seconds * 20);
                                    }
                                }
                                else {
                                    player.sendMessage(ChatColor.RED + "Usage: /card name (character-name)");
                                }
                            }
                            else {
                                player.sendMessage(ChatColor.RED + "You must wait before changing your name again!");
                            }
                        }

                }
            }
            else {
                player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need the following permission: 'rp.card.name'");
            }

        }
    }

    public static void changeRace(CommandSender sender, String[] args, ArrayList<CharacterCard> cards) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.race") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : cards) {

                    if (card.getPlayerUUID().equals(player.getUniqueId())) {

                        if (args.length > 1) {
                            card.setRace(args[1]);
                            player.sendMessage(ChatColor.GREEN + "Race set! Type /card to see changes.");
                        }
                        else {
                            player.sendMessage(ChatColor.RED + "Usage: /card race (character-race)");
                        }
                    }
                }
            }
            else {
                player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need the following permission: 'rp.card.race'");
            }

        }
    }

    public static void changeSubculture(CommandSender sender, String[] args, ArrayList<CharacterCard> cards) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.subculture") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : cards) {

                    if (card.getPlayerUUID().equals(player.getUniqueId())) {

                        if (args.length > 1) {
                            card.setSubculture(args[1]);
                            player.sendMessage(ChatColor.GREEN + "Subculture set! Type /card to see changes.");
                        }
                        else {
                            player.sendMessage(ChatColor.RED + "Usage: /card subculture (character-subculture)");
                        }
                    }
                }
            }
            else {
                player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need the following permission: 'rp.card.subculture'");
            }

        }
    }

    public static void changeReligion(CommandSender sender, String[] args, ArrayList<CharacterCard> cards) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.religion") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : cards) {

                    if (card.getPlayerUUID().equals(player.getUniqueId())) {

                        if (args.length > 1) {
                            card.setReligion(args[1]);
                            player.sendMessage(ChatColor.GREEN + "Religion set! Type /card to see changes.");
                        }
                        else {
                            player.sendMessage(ChatColor.RED + "Usage: /card religion (character-religion)");
                        }
                    }
                }
            }
            else {
                player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need the following permission: 'rp.card.religion'");
            }

        }
    }

    public static void changeAge(CommandSender sender, String[] args, ArrayList<CharacterCard> cards) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.age") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : cards) {

                    if (card.getPlayerUUID().equals(player.getUniqueId())) {

                        if (args.length > 1) {
                            card.setAge(Integer.parseInt(args[1]));
                            player.sendMessage(ChatColor.GREEN + "Age set! Type /card to see changes.");
                        }
                        else {
                            player.sendMessage(ChatColor.RED + "Usage: /card age (character-age)");
                        }
                    }
                }
            }
            else {
                player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need the following permission: 'rp.card.age'");
            }

        }
    }

    public static void changeGender(CommandSender sender, String[] args, ArrayList<CharacterCard> cards) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.gender") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : cards) {

                    if (card.getPlayerUUID().equals(player.getUniqueId())) {

                        if (args.length > 1) {
                            card.setGender(args[1]);
                            player.sendMessage(ChatColor.GREEN + "Gender set! Type /card to see changes.");
                        }
                        else {
                            player.sendMessage(ChatColor.RED + "Usage: /card gender (character-gender)");
                        }
                    }
                }
            }
            else {
                player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need the following permission: 'rp.card.gender'");
            }

        }
    }

    public static void showPlayerInfo(CommandSender sender, String[] args, ArrayList<CharacterCard> cards) {
        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (player.hasPermission("rp.card.show.others") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : cards) {
                    if (args.length > 0) {
                        if (card.getPlayerUUID().equals(Utilities.findUUIDBasedOnPlayerName(args[0]))) {
                            sender.sendMessage(ChatColor.BOLD + "" + ChatColor.AQUA + "\n----------\n" + "Character Card of " + Bukkit.getOfflinePlayer(card.getPlayerUUID()).getName() + "\n----------\n");
                            sender.sendMessage(ChatColor.AQUA + "Name: " + card.getName());
                            sender.sendMessage(ChatColor.AQUA + "Race: " + card.getRace());
                            sender.sendMessage(ChatColor.AQUA + "Subculture: " + card.getSubculture());
                            sender.sendMessage(ChatColor.AQUA + "Age: " + card.getAge());
                            sender.sendMessage(ChatColor.AQUA + "Gender: " + card.getGender());
                            sender.sendMessage(ChatColor.AQUA + "Religion: " + card.getReligion());
                            return;
                        }
                    }
                }

                player.sendMessage(ChatColor.RED + "That player wasn't found!");

            }
            else {
                player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need the following permission: 'rp.card.show.others'");
            }

        }

    }
}
