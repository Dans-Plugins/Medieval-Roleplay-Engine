package rpsystem;

public class UtilityFunctions {
    public static String createStringFromFirstArgOnwards(String[] args) {
        StringBuilder name = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            name.append(args[i]);
            if (!(i == args.length - 1)) {
                name.append(" ");
            }
        }
        return name.toString();
    }

    public void sendMessageToPlayersWithinDistance(Player player, String message, int distance) {
        Location playerLocation = player.getLocation();

        // for every online player
        for (Player potentialPlayer : getServer().getOnlinePlayers()) {

            // if in world
            if (potentialPlayer.getLocation().getWorld().getName() == playerLocation.getWorld().getName()) {

                // if within 30 blocks
                if (potentialPlayer.getLocation().distance(playerLocation) < 30) {
                    potentialPlayer.sendMessage(player.getName() + ": " + message);
                }
            }
        }

        // send player their own message
        player.sendMessage(player.getName() + ": " + message);
    }

}
