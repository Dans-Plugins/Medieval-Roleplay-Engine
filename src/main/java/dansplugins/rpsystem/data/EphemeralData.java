package dansplugins.rpsystem.data;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 */
public class EphemeralData {
    private final ArrayList<UUID> playersWithBusyBirds = new ArrayList<>();
    private final ArrayList<UUID> playersSpeakingInLocalChat = new ArrayList<>();
    private final ArrayList<UUID> playersOnNameChangeCooldown = new ArrayList<>();
    private final ArrayList<UUID> playersWithRightClickCooldown = new ArrayList<>();
    private final ArrayList<UUID> playersWhoHaveHiddenGlobalChat = new ArrayList<>();
    private final ArrayList<UUID> playersWhoHaveHiddenLocalChat = new ArrayList<>();
    private final ArrayList<UUID> playersWhoHaveHiddenLocalOOCChat = new ArrayList<>();

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

    public ArrayList<UUID> getPlayersWhoHaveHiddenLocalOOCChat() {
        return playersWhoHaveHiddenLocalOOCChat;
    }
}