package dansplugins.rpsystem.commands;

import dansplugins.factionsystem.MedievalFactionsAPI;
import dansplugins.rpsystem.MedievalFactionsIntegrator;
import dansplugins.rpsystem.data.EphemeralData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GlobalChatCommand {

    public boolean startChattingInGlobalChat(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (!(player.hasPermission("rp.global") || player.hasPermission("rp.ooc") || player.hasPermission("rp.default"))) {
            player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need one the following permissions: 'rp.global', 'rp.ooc'");
            return false;
        }

        if (args.length != 0) {
            if (args[0].equalsIgnoreCase("hide")) {
                addToPlayersWhoHaveHiddenGlobalChat(player);
                return true;
            }
            if (args[0].equalsIgnoreCase("show")) {
                removeFromPlayersWhoHaveHiddenGlobalChat(player);
                return true;
            }
        }

        // remove player from local chat
        removePlayerFromLocalChat(player);

        // remove player from faction chat if medieval factions is installed
        removePlayerFromFactionChatIfMedievalFactionsIsInstalled(player.getUniqueId());

        return true;
    }

    private void removePlayerFromLocalChat(Player player) {
        if (EphemeralData.getInstance().getPlayersSpeakingInLocalChat().contains(player.getUniqueId())) {
            EphemeralData.getInstance().getPlayersSpeakingInLocalChat().remove(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "You are now talking in global chat.");
        }
        else {
            player.sendMessage(ChatColor.RED + "You're already talking in global chat!");
        }
    }

    private void addToPlayersWhoHaveHiddenGlobalChat(Player player) {
        if (!EphemeralData.getInstance().getPlayersWhoHaveHiddenGlobalChat().contains(player.getUniqueId())) {
            EphemeralData.getInstance().getPlayersWhoHaveHiddenGlobalChat().add(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Global chat is now hidden!");
        }
        else {
            player.sendMessage(ChatColor.RED + "Global chat is already hidden!");
        }
    }

    private void removeFromPlayersWhoHaveHiddenGlobalChat(Player player) {
        if (EphemeralData.getInstance().getPlayersWhoHaveHiddenGlobalChat().contains(player.getUniqueId())) {
            EphemeralData.getInstance().getPlayersWhoHaveHiddenGlobalChat().remove(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Global chat is now visible!");
        }
        else {
            player.sendMessage(ChatColor.RED + "Global chat is already visible!");
        }
    }

    private void removePlayerFromFactionChatIfMedievalFactionsIsInstalled(UUID uuid) {
        MedievalFactionsAPI mf_api = MedievalFactionsIntegrator.getInstance().getAPI();
        if (mf_api != null) {
            mf_api.forcePlayerToLeaveFactionChat(uuid);
        }
    }

}
