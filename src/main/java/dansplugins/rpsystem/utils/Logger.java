package dansplugins.rpsystem.utils;

import dansplugins.rpsystem.MedievalRoleplayEngine;

public class Logger {

    private static Logger instance;

    private Logger() {

    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void log(String message) {
        if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) {
            System.out.println("[MRE] " + message);
        }
    }

}
