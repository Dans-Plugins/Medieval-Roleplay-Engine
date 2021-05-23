package dansplugins.rpsystem.commands;

import dansplugins.factionsystem.MedievalFactionsAPI;
import dansplugins.rpsystem.MedievalFactionsIntegrator;
import dansplugins.rpsystem.data.EphemeralData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LocalChatCommand {

    public boolean startChattingInLocalChat(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (!(player.hasPermission("rp.local") || player.hasPermission("rp.rp") || player.hasPermission("rp.default"))) {
            player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need one the following permissions: 'rp.local', 'rp.rp'");
            return false;
        }

        if (args.length != 0) {
            if (args[0].equalsIgnoreCase("hide")) {
                addToPlayersWhoHaveHiddenLocalChat(player);
                return true;
            }
            if (args[0].equalsIgnoreCase("show")) {
                removeFromPlayersWhoHaveHiddenLocalChat(player);
                return true;
            }
        }

        // add player to local chat
        addPlayerToLocalChat(player);

        // remove player from faction chat if medieval factions is installed
        removePlayerFromFactionChatIfMedievalFactionsIsInstalled(player.getUniqueId());

        return true;
    }

    private void addPlayerToLocalChat(Player player) {
        if (!EphemeralData.getInstance().getPlayersSpeakingInLocalChat().contains(player.getUniqueId())) {
            EphemeralData.getInstance().getPlayersSpeakingInLocalChat().add(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "You are now talking in local chat.");
        }
        else {
            player.sendMessage(ChatColor.RED + "You're already talking in local chat!");
        }
    }

    private void addToPlayersWhoHaveHiddenLocalChat(Player player) {
        if (!EphemeralData.getInstance().getPlayersWhoHaveHiddenLocalChat().contains(player.getUniqueId())) {
            EphemeralData.getInstance().getPlayersWhoHaveHiddenLocalChat().add(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Local chat is now hidden!");
        }
        else {
            player.sendMessage(ChatColor.RED + "Local chat is already hidden!");
        }
    }

    private void removeFromPlayersWhoHaveHiddenLocalChat(Player player) {
        if (EphemeralData.getInstance().getPlayersWhoHaveHiddenLocalChat().contains(player.getUniqueId())) {
            EphemeralData.getInstance().getPlayersWhoHaveHiddenLocalChat().remove(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Local chat is now visible!");
        }
        else {
            player.sendMessage(ChatColor.RED + "Local chat is already visible!");
        }
    }

    private void removePlayerFromFactionChatIfMedievalFactionsIsInstalled(UUID uuid) {
        MedievalFactionsAPI mf_api = MedievalFactionsIntegrator.getInstance().getAPI();
        if (mf_api != null) {
            mf_api.forcePlayerToLeaveFactionChat(uuid);
        }
    }

}
