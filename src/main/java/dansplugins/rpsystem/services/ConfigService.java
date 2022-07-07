package dansplugins.rpsystem.services;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author Daniel McCoy Stephenson
 */
public class ConfigService {
    private final MedievalRoleplayEngine medievalRoleplayEngine;

    public ConfigService(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    public FileConfiguration getConfig() {
        return medievalRoleplayEngine.getConfig();
    }

    public boolean isSet(String option) {
        return getConfig().isSet(option);
    }

    public int getInt(String option) {
        return getConfig().getInt(option);
    }

    public int getIntOrDefault(String option, int defaultValue) {
        int toReturn = getInt(option);
        if (toReturn == 0) {
            return defaultValue;
        }
        return toReturn;
    }

    public boolean getBoolean(String option) {
        return getConfig().getBoolean(option);
    }

    public double getDouble(String option) {
        return getConfig().getDouble(option);
    }

    public double getDoubleOrDefault(String option, double defaultValue) {
        double toReturn = getDouble(option);
        if (toReturn == 0) {
            return defaultValue;
        }
        return toReturn;
    }

    public String getString(String option) {
        return getConfig().getString(option);
    }

    public String getStringOrDefault(String option, String defaultValue) {
        String toReturn = getString(option);
        if (toReturn == null) {
            return defaultValue;
        }
        return toReturn;
    }
}