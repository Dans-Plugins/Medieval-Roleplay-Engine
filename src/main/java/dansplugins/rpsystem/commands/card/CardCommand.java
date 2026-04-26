package dansplugins.rpsystem.commands.card;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.cards.CharacterCard;
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
        if (!(sender instanceof Player)) {
            return;
        }
        Player player = (Player) sender;

        if (player.hasPermission("rp.card.show") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
            CharacterCard card = medievalRoleplayEngine.cardLookupService.lookup(player.getUniqueId());
            if (card == null) {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "You do not have a character card to view.");
                return;
            }
            medievalRoleplayEngine.messenger.sendCardInfoToPlayer(card, player);
        }
        else {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.card.show'");
        }
    }

    public void showHelpMessage(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return;
        }
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

    public void changeName(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return;
        }
        Player player = (Player) sender;

        if (!(player.hasPermission("rp.card.name") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default"))) {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.card.name'");
            return;
        }

        CharacterCard card = findCardForPlayer(player.getUniqueId());
        if (card == null) {
            return;
        }

        if (medievalRoleplayEngine.ephemeralData.getPlayersOnNameChangeCooldown().contains(player.getUniqueId())) {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "You must wait before changing your name again!");
            return;
        }

        if (args.length < 2) {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Usage: /card name (character-name)");
            return;
        }

        card.setName(medievalRoleplayEngine.argumentParser.createStringFromFirstArgOnwards(args, 1));
        player.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor() + "Name set! Type /card to see changes.");

        int changeNameCooldown = medievalRoleplayEngine.getConfig().getInt("changeNameCooldown");
        if (changeNameCooldown != 0) {
            medievalRoleplayEngine.ephemeralData.getPlayersOnNameChangeCooldown().add(player.getUniqueId());
            getServer().getScheduler().runTaskLater(medievalRoleplayEngine, new Runnable() {
                @Override
                public void run() {
                    medievalRoleplayEngine.ephemeralData.getPlayersOnNameChangeCooldown().remove(player.getUniqueId());
                    player.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor() + "You can now change your character's name again.");
                }
            }, changeNameCooldown * 20);
        }
    }

    public void changeRace(CommandSender sender, String[] args) {
        applyStringCardChange(sender, args, "rp.card.race", "race",
                (card, value) -> card.setRace(value));
    }

    public void changeSubculture(CommandSender sender, String[] args) {
        applyStringCardChange(sender, args, "rp.card.subculture", "subculture",
                (card, value) -> card.setSubculture(value));
    }

    public void changeReligion(CommandSender sender, String[] args) {
        applyStringCardChange(sender, args, "rp.card.religion", "religion",
                (card, value) -> card.setReligion(value));
    }

    public void changeGender(CommandSender sender, String[] args) {
        applyStringCardChange(sender, args, "rp.card.gender", "gender",
                (card, value) -> card.setGender(value));
    }

    public void changeAge(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return;
        }
        Player player = (Player) sender;

        if (!(player.hasPermission("rp.card.age") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default"))) {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.card.age'");
            return;
        }

        CharacterCard card = findCardForPlayer(player.getUniqueId());
        if (card == null) {
            return;
        }

        if (args.length < 2) {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Usage: /card age (character-age)");
            return;
        }

        int newAge;
        try {
            newAge = Integer.parseInt(medievalRoleplayEngine.argumentParser.createStringFromFirstArgOnwards(args, 1));
        } catch (NumberFormatException e) {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Must be a number.");
            return;
        }
        card.setAge(newAge);
        player.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor() + "Age set! Type /card to see changes.");
    }

    public void showPlayerInfo(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return;
        }
        Player player = (Player) sender;

        if (!(player.hasPermission("rp.card.lookup") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default"))) {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.card.lookup'");
            return;
        }

        if (args.length < 2) {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Usage: /card lookup (playerName)");
            return;
        }

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

    public boolean forceSave(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }
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

    public boolean forceLoad(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }
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

    private void applyStringCardChange(CommandSender sender, String[] args, String permissionNode,
                                        String fieldName, CardFieldSetter setter) {
        if (!(sender instanceof Player)) {
            return;
        }
        Player player = (Player) sender;

        if (!(player.hasPermission(permissionNode) || player.hasPermission("rp.card.*") || player.hasPermission("rp.default"))) {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor()
                    + "Sorry! In order to use this command, you need the following permission: '" + permissionNode + "'");
            return;
        }

        CharacterCard card = findCardForPlayer(player.getUniqueId());
        if (card == null) {
            return;
        }

        if (args.length < 2) {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor()
                    + "Usage: /card " + fieldName + " (character-" + fieldName + ")");
            return;
        }

        setter.apply(card, medievalRoleplayEngine.argumentParser.createStringFromFirstArgOnwards(args, 1));
        player.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor()
                + capitalize(fieldName) + " set! Type /card to see changes.");
    }

    private CharacterCard findCardForPlayer(UUID playerUUID) {
        return medievalRoleplayEngine.cardRepository.getCard(playerUUID);
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    @FunctionalInterface
    private interface CardFieldSetter {
        void apply(CharacterCard card, String value);
    }
}
