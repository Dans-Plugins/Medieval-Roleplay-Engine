package dansplugins.rpsystem;

import dansplugins.factionsystem.MedievalFactions;

import dansplugins.factionsystem.MedievalFactionsAPI;
import org.bukkit.plugin.PluginManager;

import static org.bukkit.Bukkit.getServer;

public class MedievalFactionsIntegrator {

    private static MedievalFactionsIntegrator instance;

    private boolean debug = true;

    private MedievalFactions mf_instance = null;
    private MedievalFactionsAPI mf_api = null;

    public static MedievalFactionsIntegrator getInstance() {
        if (instance == null) {
            instance = new MedievalFactionsIntegrator();
        }
        return instance;
    }

    public MedievalFactionsAPI getAPI() {
        return mf_api;
    }

    private MedievalFactionsIntegrator() {
        PluginManager pluginManager = getServer().getPluginManager();

        // get instance of Medieval Factions
        mf_instance = MedievalFactions.getInstance();

        if (isMedievalFactionsPresent()) {
            if (debug) {
                System.out.println("[DEBUG] Medieval Factions was found successfully!");
            }
            mf_api = mf_instance.getAPI();
        }
        else {
            if (debug) {
                System.out.println("[DEBUG] Medieval Factions was not found!");
            }
        }

    }

    private boolean isMedievalFactionsPresent() {
        return mf_instance != null;
    }

}
