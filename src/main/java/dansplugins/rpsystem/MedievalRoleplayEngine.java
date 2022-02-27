package dansplugins.rpsystem;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

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
import dansplugins.rpsystem.services.LocalConfigService;
import dansplugins.rpsystem.services.LocalStorageService;
import preponderous.ponder.minecraft.bukkit.PonderMC;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.bukkit.abs.PonderBukkitPlugin;
import preponderous.ponder.minecraft.bukkit.services.CommandService;
import preponderous.ponder.minecraft.bukkit.tools.EventHandlerRegistry;

/**
 * @author Daniel McCoy Stephenson
 */
public class MedievalRoleplayEngine extends PonderBukkitPlugin {
    private static MedievalRoleplayEngine instance;
    private final String pluginVersion = "v" + getDescription().getVersion();
    private final CommandService commandService = new CommandService((PonderMC) getPonder());
    private boolean versionMismatchOccurred;
    private String oldVersion = null;

    /**
     * This can be used to get the instance of the main class that is managed by itself.
     * @return The managed instance of the main class.
     */
    public static MedievalRoleplayEngine getInstance() {
        return instance;
    }

    /**
     * This runs when the server starts.
     */
    @Override
    public void onEnable() {
        instance = this;
        setVersionMismatchOccurred();
        handlebStatsIntegration();
        initializeConfig();
        registerEventHandlers();
        initializeCommandService();
        LocalStorageService.getInstance().load();
        handlePlaceholderIntegration();
    }

    /**
     * This runs when the server stops.
     */
    @Override
    public void onDisable() {
        LocalStorageService.getInstance().save();
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
            DefaultCommand defaultCommand = new DefaultCommand();
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
        return LocalConfigService.getInstance().getBoolean("debugMode");
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
            new PlaceholderAPI().register();
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
            LocalConfigService.getInstance().saveMissingConfigDefaultsIfNotPresent();
        }
    }

    private boolean configFileExists() {
        return new File("./plugins/" + getName() + "/config.yml").exists();
    }

    private void performCompatibilityChecks() {
        if (isVersionMismatched()) {
            LocalConfigService.getInstance().saveMissingConfigDefaultsIfNotPresent();
        }
        reloadConfig();
    }

    private void registerEventHandlers() {
        EventHandlerRegistry eventHandlerRegistry = new EventHandlerRegistry();
        ArrayList<Listener> listeners = new ArrayList<>();
        listeners.add(new ChatHandler());
        listeners.add(new InteractionHandler());
        listeners.add(new JoinHandler());
        eventHandlerRegistry.registerEventHandlers(listeners, this);
    }

    private void initializeCommandService() {
        ArrayList<AbstractPluginCommand> commands = new ArrayList<>(Arrays.asList(
                new BirdCommand(), new CardCommand(), new CharCommand(),
                new ConfigCommand(), new EmoteCommand(), new ForceCommand(),
                new GlobalChatCommand(), new HelpCommand(), new LocalChatCommand(),
                new LocalOOCChatCommand(), new RollCommand(), new SetCommand(),
                new StatsCommand(), new TitleCommand(), new UnsetCommand(),
                new WhisperCommand(), new YellCommand()
        ));
        commandService.initialize(commands, "That command wasn't found.");
    }
}