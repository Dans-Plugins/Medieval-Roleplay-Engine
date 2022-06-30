package dansplugins.rpsystem.utils;

import dansplugins.rpsystem.MedievalRoleplayEngine;

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
            System.out.println("[MRE] " + message);
        }
    }
}