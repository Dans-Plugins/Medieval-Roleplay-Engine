package plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.Commands.CardCommand;

import java.util.ArrayList;

public class Main extends JavaPlugin implements Listener {

    ArrayList<CharacterCard> cards = new ArrayList<>();

    @Override
    public void onEnable() {
        System.out.println("Medieval Roleplay Engine plugin enabling....");

        this.getServer().getPluginManager().registerEvents(this, this);

        System.out.println("Medieval Roleplay Engine plugin enabled.");
    }

    @Override
    public void onDisable() {
        System.out.println("Medieval Roleplay Engine plugin disabling....");

        System.out.println("Medieval Roleplay Engine plugin disabled.");
    }

    public void saveFileNames() {

    }

    public void saveCards() {

    }

    public void loadCards() {

    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (label.equalsIgnoreCase("card")) {
            CardCommand.showCard(sender, args, cards);
        }

        return false;
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

}