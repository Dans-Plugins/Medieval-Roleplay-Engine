package dansplugins.rpsystem;

import dansplugins.factionsystem.MedievalFactions;
import dansplugins.factionsystem.externalapi.MedievalFactionsAPI;
import org.bukkit.Bukkit;

public class MedievalFactionsIntegrator {

    private static MedievalFactionsIntegrator instance;

    private boolean debug = false;

    private MedievalFactionsAPI mf_api = null;

    private MedievalFactionsIntegrator() {
        if (isMedievalFactionsPresent()) {
            if (debug) {
                System.out.println("[DEBUG] Medieval Factions was found successfully!");
            }
            mf_api = new MedievalFactionsAPI();
        }
        else {
            if (debug) {
                System.out.println("[DEBUG] Medieval Factions was not found!");
            }
        }
    }

    public static MedievalFactionsIntegrator getInstance() {
        if (instance == null) {
            instance = new MedievalFactionsIntegrator();
        }
        return instance;
    }

    public boolean isMedievalFactionsPresent() {
        return (Bukkit.getServer().getPluginManager().getPlugin("MedievalFactions") != null);
    }

    public MedievalFactionsAPI getAPI() {
        return mf_api;
    }

}
