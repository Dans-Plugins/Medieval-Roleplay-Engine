package dansplugins.rpsystem.commands;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.Messenger;
import dansplugins.rpsystem.data.EphemeralData;
import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.managers.StorageManager;
import dansplugins.rpsystem.objects.CharacterCard;
import dansplugins.rpsystem.services.CardLookupService;
import dansplugins.rpsystem.utils.ArgumentParser;
import dansplugins.rpsystem.utils.ColorChecker;
import dansplugins.rpsystem.utils.UUIDChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class OldCardCommand {

    public void showCard(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.show") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                CharacterCard card = CardLookupService.getInstance().lookup(player.getUniqueId());
                Messenger.getInstance().sendCardInfoToPlayer(card, player);
            }
            else {
                player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.card.show'");
            }

        }
    }

    public void showHelpMessage(CommandSender sender) {
        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (player.hasPermission("rp.card.help") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                sender.sendMessage(ChatColor.BOLD + "" + ColorChecker.getInstance().getNeutralAlertColor() + " == " + "Character Card Commands" + " == ");
                sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/card - View your character card.");
                sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/card lookup (player) - View the character card of a specific player.");
                sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/card name (name) - Change your character's name.");
                sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/card race (race) - Change your character's race.");
                sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/card subculture (subculture) - Change your character's subculture.");
                sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/card age (age) - Change your character's age.");
                sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/card gender (gender) - Change your character's gender.");
                sender.sendMessage(ColorChecker.getInstance().getNeutralAlertColor() + "/card religion (religion) - Change your character's religion.");
            }
            else {
                player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.card.help'");
            }
        }

    }

    public void changeName(CommandSender sender, String[] args) {

        int changeNameCooldown = MedievalRoleplayEngine.getInstance().getConfig().getInt("changeNameCooldown");

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.name") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : PersistentData.getInstance().getCards()) {

                    if (card.getPlayerUUID().equals(player.getUniqueId())) {

                        if (!EphemeralData.getInstance().getPlayersOnNameChangeCooldown().contains(player.getUniqueId())) {

                            if (args.length > 1) {
                                card.setName(ArgumentParser.getInstance().createStringFromFirstArgOnwards(args, 1));
                                player.sendMessage(ColorChecker.getInstance().getPositiveAlertColor() + "Name set! Type /card to see changes.");

                                if (changeNameCooldown != 0) {
                                    // cooldown
                                    EphemeralData.getInstance().getPlayersOnNameChangeCooldown().add(player.getUniqueId());

                                    int seconds = changeNameCooldown;
                                    getServer().getScheduler().runTaskLater(MedievalRoleplayEngine.getInstance(), new Runnable() {
                                        @Override
                                        public void run() {
                                            EphemeralData.getInstance().getPlayersOnNameChangeCooldown().remove(player.getUniqueId());
                                            player.sendMessage(ColorChecker.getInstance().getPositiveAlertColor() + "You can now change your character's name again.");
                                        }
                                    }, seconds * 20);
                                }
                            }
                            else {
                                player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Usage: /card name (character-name)");
                            }
                        }
                        else {
                            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "You must wait before changing your name again!");
                        }
                    }

                }
            }
            else {
                player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.card.name'");
            }

        }
    }

    public void changeRace(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.race") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : PersistentData.getInstance().getCards()) {

                    if (card.getPlayerUUID().equals(player.getUniqueId())) {

                        if (args.length > 1) {
                            card.setRace(args[1]);
                            player.sendMessage(ColorChecker.getInstance().getPositiveAlertColor() + "Race set! Type /card to see changes.");
                        }
                        else {
                            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Usage: /card race (character-race)");
                        }
                    }
                }
            }
            else {
                player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.card.race'");
            }

        }
    }

    public void changeSubculture(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.subculture") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : PersistentData.getInstance().getCards()) {

                    if (card.getPlayerUUID().equals(player.getUniqueId())) {

                        if (args.length > 1) {
                            card.setSubculture(args[1]);
                            player.sendMessage(ColorChecker.getInstance().getPositiveAlertColor() + "Subculture set! Type /card to see changes.");
                        }
                        else {
                            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Usage: /card subculture (character-subculture)");
                        }
                    }
                }
            }
            else {
                player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.card.subculture'");
            }

        }
    }

    public void changeReligion(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.religion") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : PersistentData.getInstance().getCards()) {

                    if (card.getPlayerUUID().equals(player.getUniqueId())) {

                        if (args.length > 1) {
                            card.setReligion(args[1]);
                            player.sendMessage(ColorChecker.getInstance().getPositiveAlertColor() + "Religion set! Type /card to see changes.");
                        }
                        else {
                            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Usage: /card religion (character-religion)");
                        }
                    }
                }
            }
            else {
                player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.card.religion'");
            }

        }
    }

    public void changeAge(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.age") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : PersistentData.getInstance().getCards()) {

                    if (card.getPlayerUUID().equals(player.getUniqueId())) {

                        if (args.length > 1) {
                            card.setAge(Integer.parseInt(args[1]));
                            player.sendMessage(ColorChecker.getInstance().getPositiveAlertColor() + "Age set! Type /card to see changes.");
                        }
                        else {
                            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Usage: /card age (character-age)");
                        }
                    }
                }
            }
            else {
                player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.card.age'");
            }

        }
    }

    public void changeGender(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.gender") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : PersistentData.getInstance().getCards()) {

                    if (card.getPlayerUUID().equals(player.getUniqueId())) {

                        if (args.length > 1) {
                            card.setGender(args[1]);
                            player.sendMessage(ColorChecker.getInstance().getPositiveAlertColor() + "Gender set! Type /card to see changes.");
                        }
                        else {
                            player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Usage: /card gender (character-gender)");
                        }
                    }
                }
            }
            else {
                player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.card.gender'");
            }

        }
    }

    public void showPlayerInfo(CommandSender sender, String[] args) {
        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (player.hasPermission("rp.card.lookup") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {

                if (args.length < 2) {
                    player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Usage: /card lookup (playerName)");
                    return;
                }

                // get UUID
                UUID targetUUID = UUIDChecker.getInstance().findUUIDBasedOnPlayerName(args[1]);
                if (targetUUID == null) {
                    player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "That player wasn't found.");
                    return;
                }

                CharacterCard card = CardLookupService.getInstance().lookup(targetUUID);

                if (card == null) {
                    player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "That player doesn't have a card.");
                    return;
                }

                Messenger.getInstance().sendCardInfoToPlayer(card, player);
            }
            else {
                player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.card.show.others'");
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
                player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.card.forcesave'");
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
                player.sendMessage(ColorChecker.getInstance().getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.card.forceload'");
                return false;
            }
        }
        return false;
    }
}
