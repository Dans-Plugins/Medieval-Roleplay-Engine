package rpsystem;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rpsystem.Commands.*;

import static rpsystem.Utilities.*;

public class CommandInterpreter {

    MedievalRoleplayEngine medievalRoleplayEngine = null;

    public CommandInterpreter(MedievalRoleplayEngine plugin) {
        medievalRoleplayEngine = plugin;
    }

    public boolean interpretCommand(CommandSender sender, String label, String[] args) {

        // help command
        if (label.equalsIgnoreCase("rphelp")) {
            HelpCommand command = new HelpCommand(medievalRoleplayEngine);
            command.showListOfCommands(sender);
            return true;
        }

        // card command
        if (label.equalsIgnoreCase("card")) {
            if (args.length == 0) {
                CardCommand.showCard(sender, args, medievalRoleplayEngine.cards);
                return true;
            } else {

                if (args[0].equalsIgnoreCase("help")) {
                    CardCommand.showHelpMessage(sender);
                    return true;
                }

                if (args[0].equalsIgnoreCase("name")) {
                    CardCommand command = new CardCommand(medievalRoleplayEngine);
                    command.changeName(sender, args, medievalRoleplayEngine.cards);
                    return true;
                }
                if (args[0].equalsIgnoreCase("race")) {
                    CardCommand.changeRace(sender, args, medievalRoleplayEngine.cards);
                    return true;
                }
                if (args[0].equalsIgnoreCase("subculture")) {
                    CardCommand.changeSubculture(sender, args, medievalRoleplayEngine.cards);
                    return true;
                }
                if (args[0].equalsIgnoreCase("religion")) {
                    CardCommand.changeReligion(sender, args, medievalRoleplayEngine.cards);
                    return true;
                }
                if (args[0].equalsIgnoreCase("age")) {
                    CardCommand.changeAge(sender, args, medievalRoleplayEngine.cards);
                    return true;
                }
                if (args[0].equalsIgnoreCase("gender")) {
                    CardCommand.changeGender(sender, args, medievalRoleplayEngine.cards);
                    return true;
                }

                if (args[0].equalsIgnoreCase("forcesave")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;

                        if (player.hasPermission("rp.card.forcesave") || player.hasPermission("rp.admin")) {
                            medievalRoleplayEngine.storage.saveCardFileNames();
                            medievalRoleplayEngine.storage.saveCards();
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
                            medievalRoleplayEngine.storage.loadCards();
                            return true;
                        }
                        else {
                            player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need the following permission: 'rp.card.forceload'");
                            return false;
                        }
                    }

                }

                CardCommand.showPlayerInfo(sender, args, medievalRoleplayEngine.cards);
                return true;
            }
        }

        if (label.equalsIgnoreCase("bird")) {
            BirdCommand command = new BirdCommand(medievalRoleplayEngine);
            command.sendBird(sender, args);
            return true;
        }

        if (label.equalsIgnoreCase("local") || label.equalsIgnoreCase("rp")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("rp.local") || player.hasPermission("rp.rp") || player.hasPermission("rp.default")) {
                    if (!medievalRoleplayEngine.playersSpeakingInLocalChat.contains(player.getUniqueId())) {
                        medievalRoleplayEngine.playersSpeakingInLocalChat.add(player.getUniqueId());
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
                    if (medievalRoleplayEngine.playersSpeakingInLocalChat.contains(player.getUniqueId())) {
                        medievalRoleplayEngine.playersSpeakingInLocalChat.remove(player.getUniqueId());
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
                        String characterName = medievalRoleplayEngine.utilities.getCard(player.getUniqueId()).getName();

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
            TitleCommand command = new TitleCommand(medievalRoleplayEngine);
            command.titleBook(sender, args);
            return true;
        }

        if (label.equalsIgnoreCase("yell")) {
            YellCommand command = new YellCommand(medievalRoleplayEngine);
            command.sendLoudMessage(sender, args);
            return true;
        }

        if (label.equalsIgnoreCase("whisper")) {
            WhisperCommand command = new WhisperCommand(medievalRoleplayEngine);
            command.sendQuietMessage(sender, args);
            return true;
        }

        return false;
    }


}
