package dansplugins.rpsystem;

import dansplugins.rpsystem.commands.*;
import dansplugins.rpsystem.data.EphemeralData;
import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.utils.ArgumentParser;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandInterpreter {

    private static CommandInterpreter instance;

    private CommandInterpreter() {

    }

    public static CommandInterpreter getInstance() {
        if (instance == null) {
            instance = new CommandInterpreter();
        }
        return instance;
    }

    public boolean interpretCommand(CommandSender sender, String label, String[] args) {

        // help command
        if (label.equalsIgnoreCase("rphelp")) {
            HelpCommand command = new HelpCommand();
            command.showListOfCommands(sender);
            return true;
        }

        // card command
        if (label.equalsIgnoreCase("card")) {
            CardCommand command = new CardCommand();
            if (args.length == 0) {
                command.showCard(sender, args, PersistentData.getInstance().getCards());
                return true;
            } else {

                if (args[0].equalsIgnoreCase("help")) {
                    command.showHelpMessage(sender);
                    return true;
                }

                if (args[0].equalsIgnoreCase("name")) {
                    command.changeName(sender, args, PersistentData.getInstance().getCards());
                    return true;
                }
                if (args[0].equalsIgnoreCase("race")) {
                    command.changeRace(sender, args, PersistentData.getInstance().getCards());
                    return true;
                }
                if (args[0].equalsIgnoreCase("subculture")) {
                    command.changeSubculture(sender, args, PersistentData.getInstance().getCards());
                    return true;
                }
                if (args[0].equalsIgnoreCase("religion")) {
                    command.changeReligion(sender, args, PersistentData.getInstance().getCards());
                    return true;
                }
                if (args[0].equalsIgnoreCase("age")) {
                    command.changeAge(sender, args, PersistentData.getInstance().getCards());
                    return true;
                }
                if (args[0].equalsIgnoreCase("gender")) {
                    command.changeGender(sender, args, PersistentData.getInstance().getCards());
                    return true;
                }

                if (args[0].equalsIgnoreCase("forcesave")) {
                    return command.forceSave(sender);
                }

                if (args[0].equalsIgnoreCase("forceload")) {
                    return command.forceLoad(sender);
                }

                command.showPlayerInfo(sender, args, PersistentData.getInstance().getCards());
                return true;
            }
        }

        if (label.equalsIgnoreCase("bird")) {
            BirdCommand command = new BirdCommand();
            command.sendBird(sender, args);
            return true;
        }

        if (label.equalsIgnoreCase("local") || label.equalsIgnoreCase("rp")) {
            LocalChatCommand command = new LocalChatCommand();
            return command.startChattingInLocalChat(sender, args);
        }

        if (label.equalsIgnoreCase("global") || label.equalsIgnoreCase("ooc")) {
            GlobalChatCommand command = new GlobalChatCommand();
            return command.startChattingInGlobalChat(sender, args);
        }

        if (label.equalsIgnoreCase("emote") || label.equalsIgnoreCase("me")) {
            EmoteCommand command = new EmoteCommand();
            return command.emoteAction(sender, args);
        }

        if (label.equalsIgnoreCase("roll") || label.equalsIgnoreCase("dice")) {
            RollCommand command = new RollCommand();
            return command.rollDice(sender, args);
        }

        if (label.equalsIgnoreCase("title")) {
            TitleCommand command = new TitleCommand();
            command.titleBook(sender, args);
            return true;
        }

        if (label.equalsIgnoreCase("yell")) {
            YellCommand command = new YellCommand();
            command.sendLoudMessage(sender, args);
            return true;
        }

        if (label.equalsIgnoreCase("whisper")) {
            WhisperCommand command = new WhisperCommand();
            command.sendQuietMessage(sender, args);
            return true;
        }

        if (label.equalsIgnoreCase("rpconfig")) {
            ConfigCommand command = new ConfigCommand();
            command.handleConfigAccess(sender, args);
            return true;
        }

        if (label.equalsIgnoreCase("lo")) {
            LocalOOCChatCommand command = new LocalOOCChatCommand();
            command.sendLocalOOCMessage(sender, args);
            return true;
        }

        return false;
    }

}
