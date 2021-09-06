package dansplugins.rpsystem;

import dansplugins.rpsystem.eventhandlers.ChatHandler;
import dansplugins.rpsystem.eventhandlers.InteractionHandler;
import dansplugins.rpsystem.eventhandlers.JoinHandler;
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

        manager.registerEvents(new ChatHandler(), mainInstance);
        manager.registerEvents(new InteractionHandler(), mainInstance);
        manager.registerEvents(new JoinHandler(), mainInstance);

    }

}
