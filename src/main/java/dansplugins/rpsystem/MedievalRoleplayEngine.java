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
import dansplugins.rpsystem.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class MedievalRoleplayEngine extends JavaPlugin {
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

    @Override
    public void onEnable() {
        // create/load config
        if (!(new File("./plugins/MedievalRoleplayEngine/config.yml").exists())) {
            configService.saveConfigDefaults();
        }
        else {
            // pre load compatibility checks
            if (isVersionMismatched()) {
                configService.handleVersionMismatch();
            }
            reloadConfig();
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

        int pluginId = 8996;
        new Metrics(this, pluginId);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPI(this).register();
        } else {
            if (isDebugEnabled()) { System.out.println("Couldn't find PlaceholderAPI, no placeholders will be available."); }
        }
    }

    @Override
    public void onDisable() {
        storageService.saveCardFileNames();
        storageService.saveCards();
        if (configService.hasBeenAltered()) {
            saveConfig();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
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