package dansplugins.rpsystem;

import dansplugins.rpsystem.bstats.Metrics;
import dansplugins.rpsystem.commands.*;
import dansplugins.rpsystem.data.EphemeralData;
import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.listeners.ChatListener;
import dansplugins.rpsystem.listeners.InteractionListener;
import dansplugins.rpsystem.listeners.JoinListener;
import dansplugins.rpsystem.placeholders.PlaceholderAPI;
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
import preponderous.ponder.minecraft.bukkit.PonderMC;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.bukkit.abs.PonderBukkitPlugin;
import preponderous.ponder.minecraft.bukkit.services.CommandService;
import preponderous.ponder.minecraft.bukkit.tools.EventHandlerRegistry;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class MedievalRoleplayEngine extends PonderBukkitPlugin {
    private final String pluginVersion = "v" + getDescription().getVersion();

    private final CommandService commandService = new CommandService(getPonder());
    private final ConfigService configService = new ConfigService(this);
    private final Logger logger = new Logger(this);
    private final PersistentData persistentData = new PersistentData();
    private final EphemeralData ephemeralData = new EphemeralData();
    private final ColorChecker colorChecker = new ColorChecker(configService);
    private final Messenger messenger = new Messenger(ephemeralData);
    private final CharacterLookupService characterLookupService = new CharacterLookupService(logger, persistentData);
    private final StorageService storageService = new StorageService(logger, persistentData);

    /**
     * This runs when the server starts.
     */
    @Override
    public void onEnable() {
        handlebStatsIntegration();
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

    private void registerEventHandlers() {
        EventHandlerRegistry eventHandlerRegistry = new EventHandlerRegistry();
        ArrayList<Listener> listeners = new ArrayList<>();
        listeners.add(new ChatListener(configService, this, ephemeralData, colorChecker, persistentData, messenger));
        listeners.add(new InteractionListener(persistentData, this, ephemeralData, configService));
        listeners.add(new JoinListener(persistentData));
        eventHandlerRegistry.registerEventHandlers(listeners, this);
    }

    private void initializeCommandService() {
        ArrayList<AbstractPluginCommand> commands = new ArrayList<>(Arrays.asList(
                new BirdCommand(colorChecker, ephemeralData, configService, this, messenger), new CardCommand(characterLookupService, colorChecker),
                new EmoteCommand(colorChecker, persistentData, messenger, configService),
                new GlobalChatCommand(configService, ephemeralData, colorChecker), new HelpCommand(colorChecker, this, configService), new LocalChatCommand(ephemeralData, colorChecker),
                new LocalOOCChatCommand(colorChecker, persistentData, messenger, ephemeralData, configService), new RollCommand(messenger, colorChecker), new SetCommand(characterLookupService, colorChecker),
                new StatsCommand(persistentData, ephemeralData), new TitleCommand(colorChecker), new UnsetCommand(characterLookupService, colorChecker),
                new WhisperCommand(colorChecker, persistentData, messenger, configService), new YellCommand(colorChecker, persistentData, messenger, configService)
        ));
        commandService.initialize(commands, "That command wasn't found.");
    }
}