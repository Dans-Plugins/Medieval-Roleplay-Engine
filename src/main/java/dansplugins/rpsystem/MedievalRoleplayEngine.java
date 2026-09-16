package dansplugins.rpsystem;

import dansplugins.rpsystem.bstats.Metrics;
import dansplugins.rpsystem.cards.CardLookupService;
import dansplugins.rpsystem.cards.CardLookupServiceImpl;
import dansplugins.rpsystem.cards.CardRepository;
import dansplugins.rpsystem.commands.CommandService;
import dansplugins.rpsystem.config.ConfigService;
import dansplugins.rpsystem.ephemeral.EphemeralData;
import dansplugins.rpsystem.listeners.ChatListener;
import dansplugins.rpsystem.listeners.InteractionListener;
import dansplugins.rpsystem.listeners.JoinListener;
import dansplugins.rpsystem.placeholders.PlaceholderAPI;
import dansplugins.rpsystem.storage.StorageService;
import dansplugins.rpsystem.trace.TraceClient;
import dansplugins.rpsystem.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collections;

public class MedievalRoleplayEngine extends JavaPlugin {
    private static final int BSTATS_PLUGIN_ID = 8996;

    private final String pluginVersion = "v" + getDescription().getVersion();

    public final CardLookupService cardLookupService = new CardLookupServiceImpl(this);
    public final CardRepository cardRepository = new CardRepository();
    public final CommandService commandService = new CommandService(this);
    public final ConfigService configService = new ConfigService(this);
    public final EphemeralData ephemeralData = new EphemeralData();
    public final Logger logger = new Logger(this);
    public final ArgumentParser argumentParser = new ArgumentParser();
    public final ColorChecker colorChecker = new ColorChecker(this);
    public final Messenger messenger = new Messenger(this);
    public final UUIDChecker uuidChecker = new UUIDChecker();
    public final StorageService storageService  = new StorageService(this);

    // A no-op until the config has been read, so a command arriving before
    // onEnable() finishes has something safe to report to.
    private TraceClient trace = TraceClient.disabled();

    @Override
    public void onEnable() {
        if (!(new File("./plugins/MedievalRoleplayEngine/config.yml").exists())) {
            configService.saveConfigDefaults();
        }
        else {
            if (isVersionMismatched()) {
                configService.handleVersionMismatch();
            }
            reloadConfig();
            configService.ensureUsageReportingBlockOnDisk();
        }

        if (storageService.oldSaveFolderPresent()) {
            storageService.legacyLoadCards();
            storageService.deleteLegacyFiles(new File("./plugins/medieval-roleplay-engine/"));
            storageService.saveCardFileNames();
            storageService.saveCards();
        }
        else {
            storageService.loadCards();
        }

        registerListeners();

        new Metrics(this, BSTATS_PLUGIN_ID);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPI(this).register();
        } else {
            if (isDebugEnabled()) { System.out.println("Couldn't find PlaceholderAPI, no placeholders will be available."); }
        }

        // usage reporting: one event now, one per command; see config.yml
        trace = TraceClient.builder(configService.getUsageReportingEndpoint(), getName())
                .key(configService.getUsageReportingKey())
                .enabled(configService.isUsageReportingEnabled())
                .serverWideConfig(getDataFolder().getParentFile())
                .logger(getLogger())
                .build();
        logUsageReportingState();
        trace.report("startup", null, Collections.singletonMap("version", getDescription().getVersion()));
    }

    // Said on every startup so an operator can see reporting is on, and why it is off, from
    // the console alone. The wording is shared by every plugin that reports to trace.
    private void logUsageReportingState() {
        if (trace.isEnabled()) {
            getLogger().info("Usage reporting is on: " + getName() + " sends its name, version and command names to "
                    + configService.getUsageReportingEndpoint()
                    + " - nothing about players or the server. Turn it off with usage-reporting.enabled: false"
                    + " in this plugin's config.yml, or for every plugin with enabled: false in"
                    + " plugins/trace/config.yml. Details: https://github.com/Stephenson-Software/trace#usage-reporting");
        } else {
            getLogger().info("Usage reporting is off (" + trace.disabledReason() + ").");
        }
    }

    @Override
    public void onDisable() {
        trace.close();

        storageService.saveCardFileNames();
        storageService.saveCards();
        if (configService.hasBeenAltered()) {
            saveConfig();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        trace.report("command", null, Collections.singletonMap("name", cmd.getName()));
        return commandService.interpretCommand(sender, label, args);
    }

    public String getVersion() {
        return pluginVersion;
    }

    public boolean isDebugEnabled() {
        return getConfig().getBoolean("debugMode");
    }

    public boolean isVersionMismatched() {
        return !getConfig().getString("version").equalsIgnoreCase(getVersion());
    }

    private void registerListeners() {
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new ChatListener(this), this);
        manager.registerEvents(new InteractionListener(this), this);
        manager.registerEvents(new JoinListener(this), this);
    }
}