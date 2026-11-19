package dansplugins.rpsystem.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UsageReportingSettingTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File configFile(String contents) throws Exception {
        File file = folder.newFile("config.yml");
        Files.write(file.toPath(), contents.getBytes(StandardCharsets.UTF_8));
        return file;
    }

    /** What JavaPlugin.getConfig() loads: the file, with the jar's config.yml as defaults. */
    private YamlConfiguration loadLikeBukkit(File file) throws Exception {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        try (InputStreamReader jar = new InputStreamReader(
                getClass().getResourceAsStream("/config.yml"), StandardCharsets.UTF_8)) {
            config.setDefaults(YamlConfiguration.loadConfiguration(jar));
        }
        return config;
    }

    @Test
    public void onlyValuesThatClearlyMeanOnAllowReporting() {
        assertTrue(UsageReportingSetting.allowsReporting(true));
        assertTrue(UsageReportingSetting.allowsReporting("true"));
        assertTrue(UsageReportingSetting.allowsReporting(" On "));
        assertTrue(UsageReportingSetting.allowsReporting("yes"));

        assertFalse(UsageReportingSetting.allowsReporting(false));
        assertFalse(UsageReportingSetting.allowsReporting("false"));
        assertFalse(UsageReportingSetting.allowsReporting("off"));
        assertFalse(UsageReportingSetting.allowsReporting("no"));
        assertFalse(UsageReportingSetting.allowsReporting("flase"));
        assertFalse(UsageReportingSetting.allowsReporting(0));
        assertFalse(UsageReportingSetting.allowsReporting(null));
    }

    @Test
    public void quotedFalseOnDiskTurnsReportingOff() throws Exception {
        // Bukkit's getBoolean() returns the jar default (true) for the string "false".
        File file = configFile("usage-reporting:\n  enabled: \"false\"\n");
        assertTrue(loadLikeBukkit(file).getBoolean(UsageReportingSetting.ENABLED_KEY));

        assertTrue(UsageReportingSetting.turnedOffOnDisk(file));
        assertFalse(UsageReportingSetting.allowsReporting(loadLikeBukkit(file).get(UsageReportingSetting.ENABLED_KEY)));
    }

    @Test
    public void aBlankEnabledValueIsAnOptOut() throws Exception {
        File file = configFile("version: v1\nusage-reporting:\n  enabled:   # off for now\n  key: k\n");
        // Bukkit drops the key, so the bundled default would win.
        assertTrue(loadLikeBukkit(file).getBoolean(UsageReportingSetting.ENABLED_KEY));

        assertTrue(UsageReportingSetting.turnedOffOnDisk(file));
    }

    @Test
    public void anEnabledKeyOutsideTheBlockIsNotTheSwitch() throws Exception {
        File file = configFile("other:\n  enabled:\nusage-reporting:\n  enabled: true\n");
        assertFalse(UsageReportingSetting.turnedOffOnDisk(file));
    }

    @Test
    public void missingBlockIsNotAnOptOut() throws Exception {
        File file = configFile("version: v1.15.0\n");
        assertNull(UsageReportingSetting.readFromDisk(file));
        assertFalse(UsageReportingSetting.turnedOffOnDisk(file));
        assertNull(UsageReportingSetting.readFromDisk(new File(folder.getRoot(), "absent.yml")));
    }

    @Test
    public void unparseableFileCountsAsOptedOut() throws Exception {
        File file = configFile("usage-reporting:\n  enabled: false\n\tbroken: [\n");
        assertTrue(UsageReportingSetting.turnedOffOnDisk(file));
    }

    @Test
    public void staleLoadedConfigCannotOverwriteAnOptOutMadeWhileRunning() throws Exception {
        File file = configFile("version: v1.17.1\nusage-reporting:\n  enabled: true\n");
        YamlConfiguration loaded = loadLikeBukkit(file);

        // The operator turns reporting off by hand while the server runs.
        Files.write(file.toPath(), "version: v1.17.1\nusage-reporting:\n  enabled: false\n"
                .getBytes(StandardCharsets.UTF_8));

        // Something later saves the loaded (stale) config, as /rpconfig set or onDisable did.
        UsageReportingSetting.keepOptOutFromDisk(loaded, file);
        loaded.save(file);

        assertEquals(Boolean.FALSE, UsageReportingSetting.readFromDisk(file));
    }

    @Test
    public void upgradeSaveKeepsAnOptOut() throws Exception {
        File file = configFile("version: v1.17.0\nusage-reporting:\n  enabled: false\n");
        YamlConfiguration loaded = loadLikeBukkit(file);
        loaded.set("version", "v1.17.2");
        loaded.options().copyDefaults(true);
        UsageReportingSetting.keepOptOutFromDisk(loaded, file);
        loaded.save(file);

        assertEquals(Boolean.FALSE, UsageReportingSetting.readFromDisk(file));
    }
}
