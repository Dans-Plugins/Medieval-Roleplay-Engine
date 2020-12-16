package rpsystem;

import org.bukkit.plugin.PluginManager;
import rpsystem.EventHandlers.AsyncPlayerChatEventHandler;
import rpsystem.EventHandlers.PlayerInteractAtEntityEventHandler;
import rpsystem.EventHandlers.PlayerJoinEventHandler;

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
