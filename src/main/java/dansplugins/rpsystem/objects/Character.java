package dansplugins.rpsystem.objects;

import preponderous.ponder.modifiers.Savable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class is intended to represent a fictional character for roleplay reasons.
 */
public class Character implements Savable {
    private UUID playerUUID;
    private HashMap<String, String> information = new HashMap<>();
    private LocalDateTime date;

    public Character(UUID playerUUID) {
        setPlayerUUID(playerUUID);
        information.put("name", "defaultName");
        information.put("race", "defaultRace");
        information.put("subculture", "defaultSubculture");
        information.put("age", "defaultAge");
        information.put("gender", "defaultGender");
        information.put("religion", "defaultReligion");
        date = LocalDateTime.now();
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public String getInfo(String key) {
        return information.get(key);
    }
    public void setInfo(String key, String value) {
        information.put(key, value);
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public Map<String, String> save() {
        // TODO: implement
        return null;
    }

    @Override
    public void load(Map<String, String> map) {
        // TODO: implement
    }
}
