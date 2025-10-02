package dansplugins.rpsystem.commands.rpnames;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.cards.CharacterCard;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RPNamesCommand {
    private final MedievalRoleplayEngine medievalRoleplayEngine;

    public RPNamesCommand(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    public boolean toggleRPNames(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;

        if (!(player.hasPermission("rp.rpnames") || player.hasPermission("rp.default"))) {
            player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Sorry! In order to use this command, you need the following permission: 'rp.rpnames'");
            return false;
        }

        if (medievalRoleplayEngine.ephemeralData.getPlayersWithRPNamesEnabled().contains(player.getUniqueId())) {
            // Disable RP names - restore original name
            medievalRoleplayEngine.ephemeralData.getPlayersWithRPNamesEnabled().remove(player.getUniqueId());
            player.setDisplayName(player.getName());
            player.setPlayerListName(player.getName());
            player.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor() + "Character names disabled. Showing Minecraft usernames.");
        } else {
            // Enable RP names - set to character name
            medievalRoleplayEngine.ephemeralData.getPlayersWithRPNamesEnabled().add(player.getUniqueId());
            CharacterCard card = medievalRoleplayEngine.cardRepository.getCard(player.getUniqueId());
            if (card != null) {
                String characterName = card.getName();
                player.setDisplayName(characterName);
                player.setPlayerListName(characterName);
                player.sendMessage(medievalRoleplayEngine.colorChecker.getPositiveAlertColor() + "Character names enabled. Showing as: " + characterName);
            } else {
                player.sendMessage(medievalRoleplayEngine.colorChecker.getNegativeAlertColor() + "Could not find your character card.");
                return false;
            }
        }

        return true;
    }

    public void updatePlayerDisplayName(Player player) {
        if (medievalRoleplayEngine.ephemeralData.getPlayersWithRPNamesEnabled().contains(player.getUniqueId())) {
            CharacterCard card = medievalRoleplayEngine.cardRepository.getCard(player.getUniqueId());
            if (card != null) {
                String characterName = card.getName();
                player.setDisplayName(characterName);
                player.setPlayerListName(characterName);
            }
        } else {
            player.setDisplayName(player.getName());
            player.setPlayerListName(player.getName());
        }
    }

}
