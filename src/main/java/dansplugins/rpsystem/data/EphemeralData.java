package dansplugins.rpsystem.data;

import java.util.ArrayList;
import java.util.UUID;

public class EphemeralData {

    private static EphemeralData instance;

    private ArrayList<UUID> playersWithBusyBirds = new ArrayList<>();
    private ArrayList<UUID> playersSpeakingInLocalChat = new ArrayList<>();
    private ArrayList<UUID> playersOnNameChangeCooldown = new ArrayList<>();
    private ArrayList<UUID> playersWithRightClickCooldown = new ArrayList<>();
    private ArrayList<UUID> playersWhoHaveHiddenGlobalChat = new ArrayList<>();
    private ArrayList<UUID> playersWhoHaveHiddenLocalChat = new ArrayList<>();

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

    public ArrayList<UUID> getPlayersWhoHaveHiddenGlobalChat() {
        return playersWhoHaveHiddenGlobalChat;
    }

    public ArrayList<UUID> getPlayersWhoHaveHiddenLocalChat() {
        return playersWhoHaveHiddenLocalChat;
    }

}
