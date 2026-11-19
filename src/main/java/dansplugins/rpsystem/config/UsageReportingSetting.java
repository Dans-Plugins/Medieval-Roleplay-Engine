package dansplugins.rpsystem.config;

import dansplugins.rpsystem.trace.TraceClient;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * Decides whether the {@code usage-reporting.enabled} setting allows reporting.
 * <p>
 * An operator who turns reporting off must find it off, every time. So the decision fails closed:
 * only a value that clearly means "on" turns reporting on, and the value written in config.yml on
 * disk is read directly, so a stale copy of the config held in memory can never override it.
 */
public final class UsageReportingSetting {
    public static final String ENABLED_KEY = "usage-reporting.enabled";

    private UsageReportingSetting() {
    }

    /**
     * Whether {@code value} turns reporting on: {@link TraceClient#allowsReporting(Object)}, which
     * fails closed. Only {@code true}, or a string reading {@code true}, {@code on}, {@code yes} or
     * {@code 1}, does. Anything else -- {@code false}, a quoted {@code "false"}, {@code off},
     * {@code no}, a typo, or nothing -- turns it off. (Bukkit's {@code getBoolean} would instead
     * return its default, {@code true}, for a quoted {@code "false"}.)
     */
    public static boolean allowsReporting(Object value) {
        return TraceClient.allowsReporting(value);
    }

    /**
     * The value of {@code usage-reporting.enabled} as written in {@code configFile} right now,
     * with no defaults applied, or {@code null} when the file or the key is missing. A file that
     * exists but cannot be parsed reads as {@link Boolean#FALSE}: whatever the operator wrote in it
     * cannot be known, so it is not overridden. (Bukkit itself loads such a file as empty, which
     * would fall through to the bundled default, true.)
     */
    public static Object readFromDisk(File configFile) {
        if (configFile == null || !configFile.isFile()) {
            return null;
        }
        YamlConfiguration onDisk = new YamlConfiguration();
        try {
            onDisk.load(configFile);
        } catch (Exception e) {
            return Boolean.FALSE;
        }
        Object value = onDisk.get(ENABLED_KEY);
        if (value == null && hasBlankEnabledLine(configFile)) {
            return Boolean.FALSE;
        }
        return value;
    }

    /**
     * Whether {@code configFile} has an {@code enabled:} line with no value (or only a comment)
     * inside its top-level {@code usage-reporting:} block. Bukkit drops a key with no value when
     * it loads the file, so the bundled {@code true} would win; but an operator who blanked the
     * switch did not ask for reporting, so it counts as off.
     */
    static boolean hasBlankEnabledLine(File configFile) {
        java.util.List<String> lines;
        try {
            lines = java.nio.file.Files.readAllLines(configFile.toPath(), java.nio.charset.StandardCharsets.ISO_8859_1);
        } catch (Exception e) {
            return false;
        }
        boolean inBlock = false;
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            boolean topLevel = !Character.isWhitespace(line.charAt(0));
            if (topLevel) {
                inBlock = trimmed.matches("usage-reporting\\s*:\\s*(#.*)?");
                continue;
            }
            if (inBlock && trimmed.matches("enabled\\s*:\\s*(#.*)?")) {
                return true;
            }
        }
        return false;
    }

    /** Whether {@code configFile} exists but cannot be parsed as YAML. */
    public static boolean isUnparseable(File configFile) {
        if (configFile == null || !configFile.isFile()) {
            return false;
        }
        try {
            new YamlConfiguration().load(configFile);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    /** Whether {@code configFile} on disk explicitly turns reporting off. */
    public static boolean turnedOffOnDisk(File configFile) {
        Object onDisk = readFromDisk(configFile);
        return onDisk != null && !allowsReporting(onDisk);
    }

    /**
     * Carries an opt-out written in {@code configFile} into {@code loaded}, so that saving
     * {@code loaded} over the file cannot turn reporting back on. {@code loaded} may be older than
     * the file: an operator can set {@code enabled: false} by hand while the server is running.
     */
    public static void keepOptOutFromDisk(Configuration loaded, File configFile) {
        if (turnedOffOnDisk(configFile)) {
            loaded.set(ENABLED_KEY, false);
        }
    }
}
