package rpsystem;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rpsystem.Commands.*;

import static rpsystem.Utilities.*;

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
            HelpCommand command = new HelpCommand(MedievalRoleplayEngine.getInstance());
            command.showListOfCommands(sender);
            return true;
        }

        // card command
        if (label.equalsIgnoreCase("card")) {
            if (args.length == 0) {
                CardCommand.showCard(sender, args, MedievalRoleplayEngine.getInstance().cards);
                return true;
            } else {

                if (args[0].equalsIgnoreCase("help")) {
                    CardCommand.showHelpMessage(sender);
                    return true;
                }

                if (args[0].equalsIgnoreCase("name")) {
                    CardCommand command = new CardCommand(MedievalRoleplayEngine.getInstance());
                    command.changeName(sender, args, MedievalRoleplayEngine.getInstance().cards);
                    return true;
                }
                if (args[0].equalsIgnoreCase("race")) {
                    CardCommand.changeRace(sender, args, MedievalRoleplayEngine.getInstance().cards);
                    return true;
                }
                if (args[0].equalsIgnoreCase("subculture")) {
                    CardCommand.changeSubculture(sender, args, MedievalRoleplayEngine.getInstance().cards);
                    return true;
                }
                if (args[0].equalsIgnoreCase("religion")) {
                    CardCommand.changeReligion(sender, args, MedievalRoleplayEngine.getInstance().cards);
                    return true;
                }
                if (args[0].equalsIgnoreCase("age")) {
                    CardCommand.changeAge(sender, args, MedievalRoleplayEngine.getInstance().cards);
                    return true;
                }
                if (args[0].equalsIgnoreCase("gender")) {
                    CardCommand.changeGender(sender, args, MedievalRoleplayEngine.getInstance().cards);
                    return true;
                }

                if (args[0].equalsIgnoreCase("forcesave")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;

                        if (player.hasPermission("rp.card.forcesave") || player.hasPermission("rp.admin")) {
                            StorageManager.getInstance().saveCardFileNames();
                            StorageManager.getInstance().saveCards();
                            return true;
                        }
                        else {
                            player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need the following permission: 'rp.card.forcesave'");
                            return false;
                        }

                    }
                }

                if (args[0].equalsIgnoreCase("forceload")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;

                        if (player.hasPermission("rp.card.forceload") || player.hasPermission("rp.admin")) {
                            StorageManager.getInstance().loadCards();
                            return true;
                        }
                        else {
                            player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need the following permission: 'rp.card.forceload'");
                            return false;
                        }
                    }

                }

                CardCommand.showPlayerInfo(sender, args, MedievalRoleplayEngine.getInstance().cards);
                return true;
            }
        }

        if (label.equalsIgnoreCase("bird")) {
            BirdCommand command = new BirdCommand(MedievalRoleplayEngine.getInstance());
            command.sendBird(sender, args);
            return true;
        }

        if (label.equalsIgnoreCase("local") || label.equalsIgnoreCase("rp")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("rp.local") || player.hasPermission("rp.rp") || player.hasPermission("rp.default")) {
                    if (!MedievalRoleplayEngine.getInstance().playersSpeakingInLocalChat.contains(player.getUniqueId())) {
                        MedievalRoleplayEngine.getInstance().playersSpeakingInLocalChat.add(player.getUniqueId());
                        player.sendMessage(ChatColor.GREEN + "You are now talking in local chat.");
                        return true;
                    }
                    else {
                        player.sendMessage(ChatColor.RED + "You're already talking in local chat!");
                        return false;
                    }
                }
                else {
                    player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need one the following permissions: 'rp.local', 'rp.rp'");
                    return false;
                }

            }
        }

        if (label.equalsIgnoreCase("global") || label.equalsIgnoreCase("ooc")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("rp.global") || player.hasPermission("rp.ooc") || player.hasPermission("rp.default")) {
                    if (MedievalRoleplayEngine.getInstance().playersSpeakingInLocalChat.contains(player.getUniqueId())) {
                        MedievalRoleplayEngine.getInstance().playersSpeakingInLocalChat.remove(player.getUniqueId());
                        player.sendMessage(ChatColor.GREEN + "You are now talking in global chat.");
                    }
                    else {
                        player.sendMessage(ChatColor.RED + "You're already talking in global chat!");
                    }
                }
                else {
                    player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need one the following permissions: 'rp.global', 'rp.ooc'");
                }

            }
        }

        if (label.equalsIgnoreCase("emote") || label.equalsIgnoreCase("me")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("rp.emote") || player.hasPermission("rp.me") || player.hasPermission("rp.default")) {
                    if (args.length > 0) {
                        String message = createStringFromFirstArgOnwards(args, 0);
                        String characterName = Utilities.getInstance().getCard(player.getUniqueId()).getName();

                        sendMessageToPlayersWithinDistance(player,ChatColor.GRAY + "" + ChatColor.ITALIC + characterName + " " + message, 25);
                    }
                }
                else {
                    player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need one the following permissions: 'rp.emote', 'rp.me'");
                    return false;
                }

            }
        }

        if (label.equalsIgnoreCase("roll") || label.equalsIgnoreCase("dice")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("rp.roll") || player.hasPermission("rp.dice") || player.hasPermission("rp.default")) {
                    if (args.length > 0) {
                        try {
                            int max = Integer.parseInt(args[0]);
                            sendMessageToPlayersWithinDistance(player,ChatColor.AQUA + "" + ChatColor.ITALIC + player.getName() + " has rolled a " + Utilities.rollDice(max) + " out of " + max + ".", 25);
                        }
                        catch(Exception ignored) {

                        }
                    }
                }
                else {
                    player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need one the following permissions: 'rp.roll', 'rp.dice'");
                }

            }
        }

        if (label.equalsIgnoreCase("title")) {
            TitleCommand command = new TitleCommand(MedievalRoleplayEngine.getInstance());
            command.titleBook(sender, args);
            return true;
        }

        if (label.equalsIgnoreCase("yell")) {
            YellCommand command = new YellCommand(MedievalRoleplayEngine.getInstance());
            command.sendLoudMessage(sender, args);
            return true;
        }

        if (label.equalsIgnoreCase("whisper")) {
            WhisperCommand command = new WhisperCommand(MedievalRoleplayEngine.getInstance());
            command.sendQuietMessage(sender, args);
            return true;
        }

        return false;
    }


}
