package dansplugins.rpsystem.integrators;

import dansplugins.exceptions.MedievalFactionsNotFoundException;
import dansplugins.factionsystem.externalapi.MedievalFactionsAPI;
import dansplugins.rpsystem.MedievalRoleplayEngine;
import org.bukkit.Bukkit;

public class MedievalFactionsIntegrator {

    private static MedievalFactionsIntegrator instance;

    private MedievalFactionsAPI mf_api = null;

    private MedievalFactionsIntegrator() throws MedievalFactionsNotFoundException {
        try {
            if (isMedievalFactionsPresent()) {
                if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("[DEBUG] Medieval Factions was found successfully!"); }
                mf_api = new MedievalFactionsAPI();
            }
            else {
                if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("[DEBUG] Medieval Factions was not found!"); }
            }
        }
        catch(Exception e) {
            throw new MedievalFactionsNotFoundException();
        }
    }

    public static MedievalFactionsIntegrator getInstance() throws MedievalFactionsNotFoundException {
        if (instance == null) {
            instance = new MedievalFactionsIntegrator();
        }
        return instance;
    }

    public boolean isMedievalFactionsPresent() {
        return (Bukkit.getServer().getPluginManager().getPlugin("MedievalFactions") != null);
    }

    public MedievalFactionsAPI getAPI() throws MedievalFactionsNotFoundException {
        if (mf_api == null) {
            throw new MedievalFactionsNotFoundException();
        }
        return mf_api;
    }

}
