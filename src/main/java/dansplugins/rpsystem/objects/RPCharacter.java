package dansplugins.rpsystem.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dansplugins.rpsystem.MedievalRoleplayEngine;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.modifiers.Cacheable;
import preponderous.ponder.modifiers.Savable;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class is intended to represent a fictional character for roleplay reasons.
 */
public class RPCharacter implements Savable, Cacheable {
    private UUID playerUUID;
    private HashMap<String, String> information = new HashMap<>();
    private LocalDateTime date;

    public RPCharacter(UUID playerUUID) {
        setPlayerUUID(playerUUID);
        information.put("name", "defaultName");
        date = LocalDateTime.now();
    }

    public RPCharacter(Map<String, String> data) {
        this.load(data);
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
        if (getInfo(key) != null) {
            information.replace(key, value);
        }
        else {
            information.put(key, value);
        }
    }

    public void unsetInfo(String key) {
        information.remove(key);
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void sendCharacterInfo(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== Character Card of " + MedievalRoleplayEngine.getInstance().getToolbox().getUUIDChecker().findPlayerNameBasedOnUUID(playerUUID));
        for (String key : information.keySet()) {
            sender.sendMessage(ChatColor.AQUA + "" + key + ": " + information.get((key)));
        }
    }

    @Override
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("playerUUID", gson.toJson(playerUUID));
        saveMap.put("information", gson.toJson(information));
        saveMap.put("date", gson.toJson(date.toString()));

        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Type stringToStringMapType = new TypeToken<HashMap<String, String>>(){}.getType();

        playerUUID = UUID.fromString(gson.fromJson(data.get("playerUUID"), String.class));
        information = gson.fromJson(data.get("information"), stringToStringMapType);
        date = LocalDateTime.parse(gson.fromJson(data.get("date"), String.class));
    }

    @Override
    public Object getKey() {
        return playerUUID;
    }
}
