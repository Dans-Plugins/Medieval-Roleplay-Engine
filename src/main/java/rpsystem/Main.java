package rpsystem;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import rpsystem.Subsystems.CommandSubsystem;
import rpsystem.Subsystems.StorageSubsystem;

import java.util.ArrayList;

import static rpsystem.UtilityFunctions.*;

public class Main extends JavaPlugin implements Listener {

    // version
    public String version = "v1.5";

    // subsystems
    public StorageSubsystem storage = new StorageSubsystem(this);
    CommandSubsystem commands = new CommandSubsystem(this);

    // saved
    public ArrayList<CharacterCard> cards = new ArrayList<>();

    // temporary
    public ArrayList<String> playersWithBusyBirds = new ArrayList<>();
    public ArrayList<String> playersSpeakingInLocalChat = new ArrayList<>();
    public ArrayList<String> playersOnNameChangeCooldown = new ArrayList<>();
    public ArrayList<String> playersWithRightClickCooldown = new ArrayList<>();

    @Override
    public void onEnable() {
        System.out.println("Medieval Roleplay Engine plugin enabling....");
        this.getServer().getPluginManager().registerEvents(this, this);
        storage.loadCards();
        System.out.println("Medieval Roleplay Engine plugin enabled.");
    }

    @Override
    public void onDisable() {
        System.out.println("Medieval Roleplay Engine plugin disabling....");
        storage.saveCardFileNames();
        storage.saveCards();
        System.out.println("Medieval Roleplay Engine plugin disabled.");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return commands.interpretCommand(sender, label, args);
    }

    @EventHandler()
    public void onJoin(PlayerJoinEvent event) {
        if (!hasCard(event.getPlayer().getName())) {
            CharacterCard newCard = new CharacterCard(event.getPlayer().getName());
            cards.add(newCard);
        }
    }

    public boolean hasCard(String playerName) {
        for (CharacterCard card : cards) {
            if (card.getPlayerName().equalsIgnoreCase(playerName)) {
                return true;
            }
        }
        return false;
    }

    public CharacterCard getCard(String playerName) {
        for (CharacterCard card : cards) {
            if (card.getPlayerName().equalsIgnoreCase(playerName)) {
                return card;
            }
        }
        return null;
    }

    @EventHandler()
    public void onChat(AsyncPlayerChatEvent event) {
        if (playersSpeakingInLocalChat.contains(event.getPlayer().getName())) {
            sendMessageToPlayersWithinDistance(event.getPlayer(), ChatColor.GRAY + "" + String.format("%s: \"%s\"", getCard(event.getPlayer().getName()).getName(), event.getMessage()), 25);
            event.setCancelled(true);
        }
    }

    @EventHandler()
    public void onRightClick(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Player) {

            Player target = (Player) event.getRightClicked();
            CharacterCard card = getCard(target.getName());

            Player player = event.getPlayer();

            if (!playersWithRightClickCooldown.contains(player.getName())) {
                playersWithRightClickCooldown.add(player.getName());

                if (player.hasPermission("rp.card.show.others") || player.hasPermission("rp.card.*") || player.hasPermission("rp.default")) {
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.AQUA + "\n == " + "Character Card of " + card.getPlayerName() + " == ");
                    player.sendMessage(ChatColor.AQUA + "Name: " + card.getName());
                    player.sendMessage(ChatColor.AQUA + "Race: " + card.getRace());
                    player.sendMessage(ChatColor.AQUA + "Subculture: " + card.getSubculture());
                    player.sendMessage(ChatColor.AQUA + "Age: " + card.getAge());
                    player.sendMessage(ChatColor.AQUA + "Gender: " + card.getGender());
                    player.sendMessage(ChatColor.AQUA + "Religion: " + card.getReligion());

                    int seconds = 2;
                    getServer().getScheduler().runTaskLater(this, new Runnable() {
                        @Override
                        public void run() {
                            playersWithRightClickCooldown.remove(player.getName());

                        }
                    }, seconds * 20);
                }

            }

        }
    }

}