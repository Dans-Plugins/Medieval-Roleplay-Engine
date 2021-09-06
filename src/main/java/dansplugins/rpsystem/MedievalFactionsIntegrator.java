package dansplugins.rpsystem;

import dansplugins.factionsystem.externalapi.MedievalFactionsAPI;
import org.bukkit.Bukkit;

public class MedievalFactionsIntegrator {

    private static MedievalFactionsIntegrator instance;

    private MedievalFactionsAPI mf_api = null;

    private MedievalFactionsIntegrator() {
        if (isMedievalFactionsPresent()) {
            if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("[DEBUG] Medieval Factions was found successfully!"); }
            mf_api = new MedievalFactionsAPI();
        }
        else {
            if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("[DEBUG] Medieval Factions was not found!"); }
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
