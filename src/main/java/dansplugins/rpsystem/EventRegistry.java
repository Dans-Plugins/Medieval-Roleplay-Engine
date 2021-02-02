package dansplugins.rpsystem;

import dansplugins.rpsystem.eventhandlers.AsyncPlayerChatEventHandler;
import dansplugins.rpsystem.eventhandlers.PlayerInteractAtEntityEventHandler;
import dansplugins.rpsystem.eventhandlers.PlayerJoinEventHandler;
import org.bukkit.plugin.PluginManager;

public class EventRegistry {

    private static EventRegistry instance;

    private EventRegistry() {

    }

    public static EventRegistry getInstance() {
        if (instance == null) {
            instance = new EventRegistry();
        }
        return instance;
    }

    public void registerEvents() {

        MedievalRoleplayEngine mainInstance = MedievalRoleplayEngine.getInstance();
        PluginManager manager = mainInstance.getServer().getPluginManager();

        manager.registerEvents(new AsyncPlayerChatEventHandler(), mainInstance);
        manager.registerEvents(new PlayerInteractAtEntityEventHandler(), mainInstance);
        manager.registerEvents(new PlayerJoinEventHandler(), mainInstance);

    }

}
