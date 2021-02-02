package dansplugins.rpsystem.data;

import java.util.ArrayList;
import java.util.UUID;

public class EphemeralData {

    private static EphemeralData instance;

    public ArrayList<UUID> playersWithBusyBirds = new ArrayList<>();
    public ArrayList<UUID> playersSpeakingInLocalChat = new ArrayList<>();
    public ArrayList<UUID> playersOnNameChangeCooldown = new ArrayList<>();
    public ArrayList<UUID> playersWithRightClickCooldown = new ArrayList<>();

    private EphemeralData() {

    }

    public static EphemeralData getInstance() {
        if (instance == null) {
            instance = new EphemeralData();
        }
        return instance;
    }

    public ArrayList<UUID> getPlayersWithBusyBirds() {
        return playersWithBusyBirds;
    }

    public ArrayList<UUID> getPlayersSpeakingInLocalChat() {
        return playersSpeakingInLocalChat;
    }

    public ArrayList<UUID> getPlayersOnNameChangeCooldown() {
        return playersOnNameChangeCooldown;
    }

    public ArrayList<UUID> getPlayersWithRightClickCooldown() {
        return playersWithRightClickCooldown;
    }

}
