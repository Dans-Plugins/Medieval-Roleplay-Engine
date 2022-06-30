package dansplugins.rpsystem;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import dansplugins.rpsystem.data.EphemeralData;
import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.integrators.MailboxesIntegrator;
import dansplugins.rpsystem.integrators.MedievalFactionsIntegrator;
import dansplugins.rpsystem.services.CharacterLookupService;
import dansplugins.rpsystem.services.ConfigService;
import dansplugins.rpsystem.services.StorageService;
import dansplugins.rpsystem.utils.ColorChecker;
import dansplugins.rpsystem.utils.Logger;
import dansplugins.rpsystem.utils.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import dansplugins.rpsystem.bstats.Metrics;
import dansplugins.rpsystem.commands.BirdCommand;
import dansplugins.rpsystem.commands.CardCommand;
import dansplugins.rpsystem.commands.CharCommand;
import dansplugins.rpsystem.commands.ConfigCommand;
import dansplugins.rpsystem.commands.DefaultCommand;
import dansplugins.rpsystem.commands.EmoteCommand;
import dansplugins.rpsystem.commands.ForceCommand;
import dansplugins.rpsystem.commands.GlobalChatCommand;
import dansplugins.rpsystem.commands.HelpCommand;
import dansplugins.rpsystem.commands.LocalChatCommand;
import dansplugins.rpsystem.commands.LocalOOCChatCommand;
import dansplugins.rpsystem.commands.RollCommand;
import dansplugins.rpsystem.commands.SetCommand;
import dansplugins.rpsystem.commands.StatsCommand;
import dansplugins.rpsystem.commands.TitleCommand;
import dansplugins.rpsystem.commands.UnsetCommand;
import dansplugins.rpsystem.commands.WhisperCommand;
import dansplugins.rpsystem.commands.YellCommand;
import dansplugins.rpsystem.eventhandlers.ChatHandler;
import dansplugins.rpsystem.eventhandlers.InteractionHandler;
import dansplugins.rpsystem.eventhandlers.JoinHandler;
import dansplugins.rpsystem.placeholders.PlaceholderAPI;
import preponderous.ponder.minecraft.bukkit.PonderMC;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.bukkit.abs.PonderBukkitPlugin;
import preponderous.ponder.minecraft.bukkit.services.CommandService;
import preponderous.ponder.minecraft.bukkit.tools.EventHandlerRegistry;

/**
 * @author Daniel McCoy Stephenson
 */
public class MedievalRoleplayEngine extends PonderBukkitPlugin {
    private final String pluginVersion = "v" + getDescription().getVersion();

    private final CommandService commandService = new CommandService((PonderMC) getPonder());
    private final ConfigService configService = new ConfigService(this);
    private final Logger logger = new Logger(this);
    private final PersistentData persistentData = new PersistentData();
    private final EphemeralData ephemeralData = new EphemeralData();
    private final ColorChecker colorChecker = new ColorChecker(configService);
    private final Messenger messenger = new Messenger(ephemeralData);
    private final CharacterLookupService characterLookupService = new CharacterLookupService(logger, persistentData);
    private final StorageService storageService = new StorageService(configService, this, logger, persistentData);
    private final MailboxesIntegrator mailboxesIntegrator = new MailboxesIntegrator(logger);
    private final MedievalFactionsIntegrator medievalFactionsIntegrator = new MedievalFactionsIntegrator(this);

    private boolean versionMismatchOccurred;
    private String oldVersion = null;

    /**
     * This runs when the server starts.
     */
    @Override
    public void onEnable() {
        setVersionMismatchOccurred();
        handlebStatsIntegration();
        initializeConfig();
        registerEventHandlers();
        initializeCommandService();
        storageService.load();
        handlePlaceholderIntegration();
    }

    /**
     * This runs when the server stops.
     */
    @Override
    public void onDisable() {
        storageService.save();
    }

    /**
     * This method handles commands sent to the minecraft server and interprets them if the label matches one of the core commands.
     * @param sender The sender of the command.
     * @param cmd The command that was sent. This is unused.
     * @param label The core command that has been invoked.
     * @param args Arguments of the core command. Often sub-commands.
     * @return A boolean indicating whether the execution of the command was successful.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            DefaultCommand defaultCommand = new DefaultCommand(this);
            return defaultCommand.execute(sender);
        }

        return commandService.interpretAndExecuteCommand(sender, label, args);
    }

    /**
     * This can be used to get the version of the plugin.
     * @return A string containing the version preceded by 'v'
     */
    public String getVersion() {
        return pluginVersion;
    }

    /**
     * Checks if the version is mismatched.
     * @return A boolean indicating if the version is mismatched.
     */
    public boolean isVersionMismatched() {
        String configVersion = this.getConfig().getString("version");
        if (configVersion == null || this.getVersion() == null) {
            return false;
        } else {
            return !configVersion.equalsIgnoreCase(this.getVersion());
        }
    }

    /**
     * Checks if debug is enabled.
     * @return Whether debug is enabled.
     */
    public boolean isDebugEnabled() {
        return configService.getBoolean("debugMode");
    }

    public String getOldVersion() {
        return oldVersion;
    }

    private void handlebStatsIntegration() {
        int pluginId = 8996;
        new Metrics(this, pluginId);
    }

    private void handlePlaceholderIntegration() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPI(this).register();
        } else {
            if (isDebugEnabled()) { System.out.println("Couldn't find PlaceholderAPI, no placeholders will be available."); }
        }
    }

    private void setVersionMismatchOccurred() {
        String configVersion = this.getConfig().getString("version");
        oldVersion = configVersion;
        if (configVersion == null || this.getVersion() == null) {
            versionMismatchOccurred = false;
        } else {
            versionMismatchOccurred = !configVersion.equalsIgnoreCase(this.getVersion());
        }
    }

    private void initializeConfig() {
        if (configFileExists()) {
            performCompatibilityChecks();
        }
        else {
            configService.saveMissingConfigDefaultsIfNotPresent();
        }
    }

    private boolean configFileExists() {
        return new File("./plugins/" + getName() + "/config.yml").exists();
    }

    private void performCompatibilityChecks() {
        if (isVersionMismatched()) {
            configService.saveMissingConfigDefaultsIfNotPresent();
        }
        reloadConfig();
    }

    private void registerEventHandlers() {
        EventHandlerRegistry eventHandlerRegistry = new EventHandlerRegistry();
        ArrayList<Listener> listeners = new ArrayList<>();
        listeners.add(new ChatHandler(configService, this, ephemeralData, colorChecker, persistentData, messenger, medievalFactionsIntegrator));
        listeners.add(new InteractionHandler(persistentData, this, ephemeralData, messenger));
        listeners.add(new JoinHandler(persistentData));
        eventHandlerRegistry.registerEventHandlers(listeners, this);
    }

    private void initializeCommandService() {
        ArrayList<AbstractPluginCommand> commands = new ArrayList<>(Arrays.asList(
                new BirdCommand(colorChecker, ephemeralData, configService, this, messenger, mailboxesIntegrator), new CardCommand(characterLookupService, colorChecker), new CharCommand(),
                new ConfigCommand(colorChecker, configService), new EmoteCommand(this, colorChecker, persistentData, messenger), new ForceCommand(),
                new GlobalChatCommand(configService, ephemeralData, colorChecker), new HelpCommand(colorChecker, this, configService), new LocalChatCommand(ephemeralData, colorChecker, medievalFactionsIntegrator),
                new LocalOOCChatCommand(colorChecker, this, persistentData, messenger, ephemeralData), new RollCommand(messenger, colorChecker), new SetCommand(characterLookupService, colorChecker),
                new StatsCommand(persistentData, ephemeralData), new TitleCommand(colorChecker), new UnsetCommand(characterLookupService, colorChecker),
                new WhisperCommand(colorChecker, this, persistentData, messenger), new YellCommand(colorChecker, this, persistentData, messenger)
        ));
        commandService.initialize(commands, "That command wasn't found.");
    }
}