package dansplugins.rpsystem.commands.card;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.cards.CharacterCard;
import dansplugins.rpsystem.storage.StorageService;
import dansplugins.rpsystem.utils.UUIDChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class CardCommand {
    private final MedievalRoleplayEngine medievalRoleplayEngine;

    public CardCommand(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    public void showCard(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.show") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                CharacterCard card = medievalRoleplayEngine.cardLookupService.lookup(player.getUniqueId());
                medievalRoleplayEngine.messenger.sendCardInfoToPlayer(card, player);
            }
            else {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.card.show'");
            }

        }
    }

    public void showHelpMessage(CommandSender sender) {
        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (player.hasPermission("rp.card.help") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                sender.sendMessage(ChatColor.BOLD + "" + medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + " == " + "Character Card Commands" + " == ");
                sender.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "/card - View your character card.");
                sender.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "/card lookup (player) - View the character card of a specific player.");
                sender.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "/card name (name) - Change your character's name.");
                sender.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "/card race (race) - Change your character's race.");
                sender.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "/card subculture (subculture) - Change your character's subculture.");
                sender.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "/card age (age) - Change your character's age.");
                sender.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "/card gender (gender) - Change your character's gender.");
                sender.sendMessage(medievalRoleplayEngine.colorChecker.getNeutralAlertColor() + "/card religion (religion) - Change your character's religion.");
            }
            else {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.card.help'");
            }
        }

    }

    public void changeName(CommandSender sender, String[] args) {

        int changeNameCooldown = medievalRoleplayEngine.getConfig().getInt("changeNameCooldown");

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.name") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : medievalRoleplayEngine.cardRepository.getCards()) {

                        if (card.getPlayerUUID().equals(player.getUniqueId())) {

                            if (!medievalRoleplayEngine.ephemeralData.getPlayersOnNameChangeCooldown().contains(player.getUniqueId())) {

                                if (args.length > 1) {
                                    card.setName(medievalRoleplayEngine.argumentParser.createStringFromFirstArgOnwards(args, 1));
                                    player.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor() + "Name set! Type /card to see changes.");

                                    if (changeNameCooldown != 0) {
                                        // cooldown
                                        medievalRoleplayEngine.ephemeralData.getPlayersOnNameChangeCooldown().add(player.getUniqueId());

                                        int seconds = changeNameCooldown;
                                        getServer().getScheduler().runTaskLater(medievalRoleplayEngine, new Runnable() {
                                            @Override
                                            public void run() {
                                                medievalRoleplayEngine.ephemeralData.getPlayersOnNameChangeCooldown().remove(player.getUniqueId());
                                                player.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor() + "You can now change your character's name again.");
                                            }
                                        }, seconds * 20);
                                    }
                                }
                                else {
                                    player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Usage: /card name (character-name)");
                                }
                            }
                            else {
                                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "You must wait before changing your name again!");
                            }
                        }

                }
            }
            else {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.card.name'");
            }

        }
    }

    public void changeRace(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.race") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : medievalRoleplayEngine.cardRepository.getCards()) {

                    if (card.getPlayerUUID().equals(player.getUniqueId())) {

                        if (args.length > 1) {
                            card.setRace(medievalRoleplayEngine.argumentParser.createStringFromFirstArgOnwards(args, 1));
                            player.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor() + "Race set! Type /card to see changes.");
                        }
                        else {
                            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Usage: /card race (character-race)");
                        }
                    }
                }
            }
            else {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.card.race'");
            }

        }
    }

    public void changeSubculture(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.subculture") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : medievalRoleplayEngine.cardRepository.getCards()) {

                    if (card.getPlayerUUID().equals(player.getUniqueId())) {

                        if (args.length > 1) {
                            card.setSubculture(medievalRoleplayEngine.argumentParser.createStringFromFirstArgOnwards(args, 1));
                            player.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor() + "Subculture set! Type /card to see changes.");
                        }
                        else {
                            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Usage: /card subculture (character-subculture)");
                        }
                    }
                }
            }
            else {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.card.subculture'");
            }

        }
    }

    public void changeReligion(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.religion") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : medievalRoleplayEngine.cardRepository.getCards()) {

                    if (card.getPlayerUUID().equals(player.getUniqueId())) {

                        if (args.length > 1) {
                            card.setReligion(medievalRoleplayEngine.argumentParser.createStringFromFirstArgOnwards(args, 1));
                            player.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor() + "Religion set! Type /card to see changes.");
                        }
                        else {
                            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Usage: /card religion (character-religion)");
                        }
                    }
                }
            }
            else {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.card.religion'");
            }

        }
    }

    public void changeAge(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.age") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : medievalRoleplayEngine.cardRepository.getCards()) {

                    if (card.getPlayerUUID().equals(player.getUniqueId())) {

                        if (args.length > 1) {
                            card.setAge(Integer.parseInt(medievalRoleplayEngine.argumentParser.createStringFromFirstArgOnwards(args, 1)));
                            player.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor() + "Age set! Type /card to see changes.");
                        }
                        else {
                            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Usage: /card age (character-age)");
                        }
                    }
                }
            }
            else {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.card.age'");
            }

        }
    }

    public void changeGender(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.gender") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                for (CharacterCard card : medievalRoleplayEngine.cardRepository.getCards()) {

                    if (card.getPlayerUUID().equals(player.getUniqueId())) {

                        if (args.length > 1) {
                            card.setGender(medievalRoleplayEngine.argumentParser.createStringFromFirstArgOnwards(args, 1));
                            player.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor() + "Gender set! Type /card to see changes.");
                        }
                        else {
                            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Usage: /card gender (character-gender)");
                        }
                    }
                }
            }
            else {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.card.gender'");
            }

        }
    }

    public void showPlayerInfo(CommandSender sender, String[] args) {
        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (player.hasPermission("rp.card.lookup") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {

                if (args.length < 2) {
                    player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Usage: /card lookup (playerName)");
                    return;
                }

                // get UUID
                UUID targetUUID = medievalRoleplayEngine.uuidChecker.findUUIDBasedOnPlayerName(medievalRoleplayEngine.argumentParser.createStringFromFirstArgOnwards(args, 1));
                if (targetUUID == null) {
                    player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "That player wasn't found.");
                    return;
                }

                CharacterCard card = medievalRoleplayEngine.cardLookupService.lookup(targetUUID);

                if (card == null) {
                    player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "That player doesn't have a card.");
                    return;
                }

                medievalRoleplayEngine.messenger.sendCardInfoToPlayer(card, player);
            }
            else {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.card.show.others'");
            }

        }

    }

    public boolean forceSave(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.forcesave") || player.hasPermission("rp.admin")) {
                medievalRoleplayEngine.storageService.saveCardFileNames();
                medievalRoleplayEngine.storageService.saveCards();
                return true;
            }
            else {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.card.forcesave'");
                return false;
            }

        }
        return false;
    }

    public boolean forceLoad(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("rp.card.forceload") || player.hasPermission("rp.admin")) {
                medievalRoleplayEngine.storageService.loadCards();
                return true;
            }
            else {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.card.forceload'");
                return false;
            }
        }
        return false;
    }
}
