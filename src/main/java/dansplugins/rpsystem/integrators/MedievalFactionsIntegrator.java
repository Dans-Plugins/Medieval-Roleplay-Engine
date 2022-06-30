package dansplugins.rpsystem.integrators;

import org.bukkit.Bukkit;

import dansplugins.factionsystem.externalapi.MedievalFactionsAPI;
import dansplugins.rpsystem.MedievalRoleplayEngine;

/**
 * @author Daniel McCoy Stephenson
 */
public class MedievalFactionsIntegrator {
    private final MedievalRoleplayEngine medievalRoleplayEngine;

    private MedievalFactionsAPI mf_api = null;

    public MedievalFactionsIntegrator(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
        if (isMedievalFactionsPresent()) {
            if (this.medievalRoleplayEngine.isDebugEnabled()) { System.out.println("[DEBUG] Medieval Factions was found successfully!"); }
            mf_api = new MedievalFactionsAPI();
        }
        else {
            if (this.medievalRoleplayEngine.isDebugEnabled()) { System.out.println("[DEBUG] Medieval Factions was not found!"); }
        }
    }

    public boolean isMedievalFactionsPresent() {
        return (Bukkit.getServer().getPluginManager().getPlugin("MedievalFactions") != null);
    }

    public MedievalFactionsAPI getAPI() {
        return mf_api;
    }
}