package dansplugins.rpsystem;

import dansplugins.rpsystem.Objects.CharacterCard;
import dansplugins.rpsystem.data.PersistentData;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

import static org.bukkit.Bukkit.*;

public class Utilities {

    private static Utilities instance;

    private Utilities() {

    }

    public static Utilities getInstance() {
        if (instance == null) {
            instance = new Utilities();
        }
        return instance;
    }

    public static String createStringFromFirstArgOnwards(String[] args, int startingArg) {
        StringBuilder name = new StringBuilder();
        for (int i = startingArg; i < args.length; i++) {
            name.append(args[i]);
            if (!(i == args.length - 1)) {
                name.append(" ");
            }
        }
        return name.toString();
    }

    public String createStringFromArgs(String[] args) {
        String toReturn = args[0];
        for (int i = 1; i < args.length; i++) {
            toReturn = toReturn + " " + args[i];
        }
        return toReturn;
    }

}
