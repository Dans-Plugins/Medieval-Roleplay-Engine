package dansplugins.rpsystem.objects;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.modifiers.Cacheable;
import preponderous.ponder.modifiers.Savable;

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

    public void sendCharacterInfo(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== Character Card of " + MedievalRoleplayEngine.getInstance().getToolbox().getUUIDChecker().findPlayerNameBasedOnUUID(playerUUID));
        for (String key : information.keySet()) {
            sender.sendMessage(ChatColor.AQUA + "" + key + ": " + information.get((key)));
        }
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

    @Override
    public Object getKey() {
        return playerUUID;
    }
}
