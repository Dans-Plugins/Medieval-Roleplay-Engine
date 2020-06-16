package rpsystem;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import rpsystem.Commands.BirdCommand;
import rpsystem.Commands.CardCommand;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import static rpsystem.UtilityFunctions.createStringFromFirstArgOnwards;
import static rpsystem.UtilityFunctions.sendMessageToPlayersWithinDistance;

public class Main extends JavaPlugin implements Listener {

    ArrayList<CharacterCard> cards = new ArrayList<>();
    public ArrayList<String> playersWithBusyBirds = new ArrayList<>();

    @Override
    public void onEnable() {
        System.out.println("Medieval Roleplay Engine plugin enabling....");

        this.getServer().getPluginManager().registerEvents(this, this);

        loadCards();

        System.out.println("Medieval Roleplay Engine plugin enabled.");
    }

    @Override
    public void onDisable() {
        System.out.println("Medieval Roleplay Engine plugin disabling....");

        saveCardFileNames();
        saveCards();

        System.out.println("Medieval Roleplay Engine plugin disabled.");
    }

    public void saveCardFileNames() {
        try {
            File saveFolder = new File("./plugins/medieval-roleplay-engine/");
            if (!saveFolder.exists()) {
                saveFolder.mkdir();
            }
            File saveFile = new File("./plugins/medieval-roleplay-engine/" + "card-player-names.txt");
            if (saveFile.createNewFile()) {
                System.out.println("Save file for character card filenames created.");
            } else {
                System.out.println("Save file for character card filenames already exists. Overwriting.");
            }

            FileWriter saveWriter = new FileWriter(saveFile);

            // actual saving takes place here
            for (CharacterCard card : cards) {
                saveWriter.write(card.getPlayerName() + "\n");
            }

            saveWriter.close();

        } catch (IOException e) {
            System.out.println("An error occurred while saving character card filenames.");
        }
    }

    public void saveCards() {
        for (CharacterCard card : cards) {
            card.save();
        }
    }

    public void loadCards() {
        try {
            System.out.println("Attempting to load character cards...");
            File loadFile = new File("./plugins/medieval-roleplay-engine/" + "card-player-names.txt");
            Scanner loadReader = new Scanner(loadFile);

            // actual loading
            while (loadReader.hasNextLine()) {
                String nextName = loadReader.nextLine();
                CharacterCard temp = new CharacterCard(nextName);
                temp.load(nextName + ".txt"); // provides owner field among other things

                // existence check
                boolean exists = false;
                for (int i = 0; i < cards.size(); i++) {
                    if (cards.get(i).getName().equalsIgnoreCase(temp.getName())) {
                        cards.remove(i);
                    }
                }

                cards.add(temp);

            }

            loadReader.close();
            System.out.println("Character cards successfully loaded.");
        } catch (FileNotFoundException e) {
            System.out.println("Error loading the character cards!");
            e.printStackTrace();
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // card command
        if (label.equalsIgnoreCase("card")) {
            if (args.length == 0) {
                CardCommand.showCard(sender, args, cards);
                return true;
            } else {

                if (args[0].equalsIgnoreCase("help")) {
                    CardCommand.showHelpMessage(sender);

                }

                if (args[0].equalsIgnoreCase("name")) {
                    CardCommand.changeName(sender, args, cards);
                    return true;
                }
                if (args[0].equalsIgnoreCase("race")) {
                    CardCommand.changeRace(sender, args, cards);
                    return true;
                }
                if (args[0].equalsIgnoreCase("subculture")) {
                    CardCommand.changeSubculture(sender, args, cards);
                    return true;
                }
                if (args[0].equalsIgnoreCase("religion")) {
                    CardCommand.changeReligion(sender, args, cards);
                    return true;
                }
                if (args[0].equalsIgnoreCase("age")) {
                    CardCommand.changeAge(sender, args, cards);
                    return true;
                }
                if (args[0].equalsIgnoreCase("gender")) {
                    CardCommand.changeGender(sender, args, cards);
                    return true;
                }

                if (args[0].equalsIgnoreCase("forcesave")) {
                    if (!(sender instanceof Player)) {
                        saveCardFileNames();
                        saveCards();
                    }
                }

                if (args[0].equalsIgnoreCase("forceload")) {
                    if (!(sender instanceof Player)) {
                        loadCards();
                    }
                }
            }

            CardCommand.showPlayerInfo(sender, args, cards);
        }

        if (label.equalsIgnoreCase("bird")) {
            BirdCommand command = new BirdCommand(this);
            command.sendBird(sender, args);
        }

        if (label.equalsIgnoreCase("local")) {
            if (args.length > 1) {
                if (sender instanceof Player) {
                    String message = createStringFromFirstArgOnwards(args);
                    sendMessageToPlayersWithinDistance((Player) sender, message, 30);
                }
            }
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