package dansplugins.rpsystem.utils;

import dansplugins.rpsystem.MedievalRoleplayEngine;

import java.util.logging.Level;

/**
 * @author Daniel McCoy Stephenson
 */
public class Logger {
    private final MedievalRoleplayEngine medievalRoleplayEngine;

    public Logger(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    public void log(String message) {
        if (medievalRoleplayEngine.isDebugEnabled()) {
            medievalRoleplayEngine.getLogger().log(Level.INFO, "[MRE] " + message);
        }
    }
}