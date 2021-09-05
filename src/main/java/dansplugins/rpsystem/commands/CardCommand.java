package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.ConfigManager;
import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.StorageManager;
import dansplugins.rpsystem.data.EphemeralData;
import dansplugins.rpsystem.objects.CharacterCard;
import dansplugins.rpsystem.utils.ArgumentParser;
import dansplugins.rpsystem.utils.ColorChecker;
import dansplugins.rpsystem.utils.UUIDChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static org.bukkit.Bukkit.getServer;

public class CardCommand {

    public void showCard(CommandSender sender, String[] args, ArrayList<CharacterCard> cards) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.show") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : cards) {
                    if (card.getPlayerUUID() != null) {
                        if (card.getPlayerUUID().equals(player.getUniqueId())) {
                            player.sendMessage(ChatColor.BOLD + "" + ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "\n----------\n" + "Character Card of " + Bukkit.getOfflinePlayer(card.getPlayerUUID()).getName() + "\n----------\n");
                            player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "Name: " + card.getName());
                            player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "Race: " + card.getRace());
                            player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "Subculture: " + card.getSubculture());
                            player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "Age: " + card.getAge());
                            player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "Gender: " + card.getGender());
                            player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "Religion: " + card.getReligion());
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

    public void showHelpMessage(CommandSender sender) {
        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (player.hasPermission("rp.card.help") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                sender.sendMessage(ChatColor.BOLD + "" + ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + " == " + "Character Card Commands" + " == ");
                sender.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "/card - View your character card.");
                sender.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "/card (player) - View the character card of a specific player.");
                sender.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "/card name (name) - Change your character's name.");
                sender.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "/card race (race) - Change your character's race.");
                sender.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "/card subculture (subculture) - Change your character's subculture.");
                sender.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "/card age (age) - Change your character's age.");
                sender.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "/card gender (gender) - Change your character's gender.");
                sender.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "/card religion (religion) - Change your character's religion.");
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

                            if (!EphemeralData.getInstance().getPlayersOnNameChangeCooldown().contains(player.getUniqueId())) {

                                if (args.length > 1) {
                                    card.setName(ArgumentParser.getInstance().createStringFromFirstArgOnwards(args, 1));
                                    player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("positiveAlertColor")) + "Name set! Type /card to see changes.");

                                    if (changeNameCooldown != 0) {
                                        // cooldown
                                        EphemeralData.getInstance().getPlayersOnNameChangeCooldown().add(player.getUniqueId());

                                        int seconds = changeNameCooldown;
                                        getServer().getScheduler().runTaskLater(MedievalRoleplayEngine.getInstance(), new Runnable() {
                                            @Override
                                            public void run() {
                                                EphemeralData.getInstance().getPlayersOnNameChangeCooldown().remove(player.getUniqueId());
                                                player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("positiveAlertColor")) + "You can now change your character's name again.");
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

    public void changeRace(CommandSender sender, String[] args, ArrayList<CharacterCard> cards) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.race") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : cards) {

                    if (card.getPlayerUUID().equals(player.getUniqueId())) {

                        if (args.length > 1) {
                            card.setRace(args[1]);
                            player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("positiveAlertColor")) + "Race set! Type /card to see changes.");
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

    public void changeSubculture(CommandSender sender, String[] args, ArrayList<CharacterCard> cards) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.subculture") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : cards) {

                    if (card.getPlayerUUID().equals(player.getUniqueId())) {

                        if (args.length > 1) {
                            card.setSubculture(args[1]);
                            player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("positiveAlertColor")) + "Subculture set! Type /card to see changes.");
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

    public void changeReligion(CommandSender sender, String[] args, ArrayList<CharacterCard> cards) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.religion") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : cards) {

                    if (card.getPlayerUUID().equals(player.getUniqueId())) {

                        if (args.length > 1) {
                            card.setReligion(args[1]);
                            player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("positiveAlertColor")) + "Religion set! Type /card to see changes.");
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

    public void changeAge(CommandSender sender, String[] args, ArrayList<CharacterCard> cards) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.age") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : cards) {

                    if (card.getPlayerUUID().equals(player.getUniqueId())) {

                        if (args.length > 1) {
                            card.setAge(Integer.parseInt(args[1]));
                            player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("positiveAlertColor")) + "Age set! Type /card to see changes.");
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

    public void changeGender(CommandSender sender, String[] args, ArrayList<CharacterCard> cards) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.gender") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : cards) {

                    if (card.getPlayerUUID().equals(player.getUniqueId())) {

                        if (args.length > 1) {
                            card.setGender(args[1]);
                            player.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("positiveAlertColor")) + "Gender set! Type /card to see changes.");
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

    public void showPlayerInfo(CommandSender sender, String[] args, ArrayList<CharacterCard> cards) {
        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (player.hasPermission("rp.card.show.others") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : cards) {
                    if (args.length > 0) {
                        if (card.getPlayerUUID().equals(UUIDChecker.getInstance().findUUIDBasedOnPlayerName(args[0]))) {
                            sender.sendMessage(ChatColor.BOLD + "" + ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "\n----------\n" + "Character Card of " + Bukkit.getOfflinePlayer(card.getPlayerUUID()).getName() + "\n----------\n");
                            sender.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "Name: " + card.getName());
                            sender.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "Race: " + card.getRace());
                            sender.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "Subculture: " + card.getSubculture());
                            sender.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "Age: " + card.getAge());
                            sender.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "Gender: " + card.getGender());
                            sender.sendMessage(ColorChecker.getInstance().getColorByName(ConfigManager.getInstance().getString("neutralAlertColor")) + "Religion: " + card.getReligion());
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

    public boolean forceSave(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.forcesave") || player.hasPermission("rp.admin")) {
                StorageManager.getInstance().saveCardFileNames();
                StorageManager.getInstance().saveCards();
                return true;
            }
            else {
                player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need the following permission: 'rp.card.forcesave'");
                return false;
            }

        }
        return false;
    }

    public boolean forceLoad(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.forceload") || player.hasPermission("rp.admin")) {
                StorageManager.getInstance().loadCards();
                return true;
            }
            else {
                player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need the following permission: 'rp.card.forceload'");
                return false;
            }
        }
        return false;
    }
}
