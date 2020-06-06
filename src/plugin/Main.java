package plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.Commands.CardCommand;

import java.util.ArrayList;

public class Main extends JavaPlugin {

    ArrayList<CharacterCard> cards = new ArrayList<>();

    @Override
    public void onEnable() {
        System.out.println("Medieval Roleplay Engine plugin enabling....");

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

}