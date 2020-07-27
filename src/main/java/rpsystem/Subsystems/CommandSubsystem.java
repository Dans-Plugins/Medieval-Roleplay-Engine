package rpsystem.Subsystems;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rpsystem.Commands.BirdCommand;
import rpsystem.Commands.CardCommand;
import rpsystem.Commands.TitleCommand;
import rpsystem.Main;

import static rpsystem.UtilityFunctions.*;

public class CommandSubsystem {

    Main main = null;

    public CommandSubsystem(Main plugin) {
        main = plugin;
    }

    public boolean interpretCommand(CommandSender sender, String label, String[] args) {

        // card command
        if (label.equalsIgnoreCase("card")) {
            if (args.length == 0) {
                CardCommand.showCard(sender, args, main.cards);
                return true;
            } else {

                if (args[0].equalsIgnoreCase("help")) {
                    CardCommand.showHelpMessage(sender);
                    return true;
                }

                if (args[0].equalsIgnoreCase("name")) {
                    CardCommand command = new CardCommand(main);
                    command.changeName(sender, args, main.cards);
                    return true;
                }
                if (args[0].equalsIgnoreCase("race")) {
                    CardCommand.changeRace(sender, args, main.cards);
                    return true;
                }
                if (args[0].equalsIgnoreCase("subculture")) {
                    CardCommand.changeSubculture(sender, args, main.cards);
                    return true;
                }
                if (args[0].equalsIgnoreCase("religion")) {
                    CardCommand.changeReligion(sender, args, main.cards);
                    return true;
                }
                if (args[0].equalsIgnoreCase("age")) {
                    CardCommand.changeAge(sender, args, main.cards);
                    return true;
                }
                if (args[0].equalsIgnoreCase("gender")) {
                    CardCommand.changeGender(sender, args, main.cards);
                    return true;
                }

                if (args[0].equalsIgnoreCase("forcesave")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;

                        if (player.hasPermission("rp.card.forcesave") || player.hasPermission("rp.admin")) {
                            main.storage.saveCardFileNames();
                            main.storage.saveCards();
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
                            main.storage.loadCards();
                            return true;
                        }
                        else {
                            player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need the following permission: 'rp.card.forceload'");
                            return false;
                        }
                    }

                }

                CardCommand.showPlayerInfo(sender, args, main.cards);
                return true;
            }
        }

        if (label.equalsIgnoreCase("bird")) {
            BirdCommand command = new BirdCommand(main);
            command.sendBird(sender, args);
            return true;
        }

        if (label.equalsIgnoreCase("local") || label.equalsIgnoreCase("rp")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("rp.local") || player.hasPermission("rp.rp") || player.hasPermission("rp.default")) {
                    if (!main.playersSpeakingInLocalChat.contains(player.getName())) {
                        main.playersSpeakingInLocalChat.add(player.getName());
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
                    if (main.playersSpeakingInLocalChat.contains(player.getName())) {
                        main.playersSpeakingInLocalChat.remove(player.getName());
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
                        String characterName = main.getCard(player.getName()).getName();

                        sendMessageToPlayersWithinDistance(player,ChatColor.GRAY + "" + ChatColor.ITALIC + characterName + " " + message, 25);
                    }
                }
                else {
                    player.sendMessage(ChatColor.RED + "Sorry! In order to use this command, you need one the following permissions: 'rp.emote', 'rp.me'");
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
                            sendMessageToPlayersWithinDistance(player,ChatColor.AQUA + "" + ChatColor.ITALIC + player.getName() + " has rolled a " + rollDice(max) + " out of " + max + ".", 25);
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
            TitleCommand command = new TitleCommand(main);
            command.titleBook(sender, args);
        }

        return false;
    }


}
