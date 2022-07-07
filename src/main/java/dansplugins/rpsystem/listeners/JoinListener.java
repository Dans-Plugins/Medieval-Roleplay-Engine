package dansplugins.rpsystem.listeners;

import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.objects.RPCharacter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author Daniel McCoy Stephenson
 */
public class JoinListener implements Listener {
    private final PersistentData persistentData;

    public JoinListener(PersistentData persistentData) {
        this.persistentData = persistentData;
    }

    @EventHandler()
    public void handle(PlayerJoinEvent event) {
        if (!persistentData.hasCharacter(event.getPlayer().getUniqueId())) {
            RPCharacter newCharacter = new RPCharacter(event.getPlayer().getUniqueId());
            persistentData.getCharacters().add(newCharacter);
        }
    }
}