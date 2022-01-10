package dansplugins.rpsystem;

import dansplugins.rpsystem.bstats.Metrics;
import dansplugins.rpsystem.commands.*;
import dansplugins.rpsystem.eventhandlers.ChatHandler;
import dansplugins.rpsystem.eventhandlers.InteractionHandler;
import dansplugins.rpsystem.eventhandlers.JoinHandler;
import dansplugins.rpsystem.placeholders.PlaceholderAPI;
import dansplugins.rpsystem.services.LocalConfigService;
import dansplugins.rpsystem.services.LocalStorageService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import preponderous.ponder.minecraft.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.abs.PonderPlugin;
import preponderous.ponder.minecraft.spigot.misc.PonderAPI_Integrator;
import preponderous.ponder.minecraft.spigot.tools.EventHandlerRegistry;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class MedievalRoleplayEngine extends PonderPlugin {
    private static MedievalRoleplayEngine instance;
    private final String version = "v2.0-alpha-10";
    private boolean versionMismatchOccurred;
    private String oldVersion = null;

    public static MedievalRoleplayEngine getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        ponderAPI_integrator = new PonderAPI_Integrator(this);

        setVersionMismatchOccurred();

        int pluginId = 8996;
        Metrics metrics = new Metrics(this, pluginId);

        initializeConfigFile();
        registerEventHandlers();
        initializeCommandService();
        getPonderAPI().setDebug(false);

        LocalStorageService.getInstance().load();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPI().register();
        } else {
            if (isDebugEnabled()) { System.out.println("Couldn't find PlaceholderAPI, no placeholders will be available."); }
        }
    }

    @Override
    public void onDisable() {
        LocalStorageService.getInstance().save();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            DefaultCommand defaultCommand = new DefaultCommand();
            return defaultCommand.execute(sender);
        }

        return getPonderAPI().getCommandService().interpretCommand(sender, label, args);
    }

    public boolean isDebugEnabled() {
        return getConfig().getBoolean("debugMode");
    }

    public String getVersion() {
        return version;
    }

    public boolean isVersionMismatched() {
        return versionMismatchOccurred;
    }

    public String getOldVersion() {
        return oldVersion;
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

    private void initializeConfigFile() {
        if (!(new File("./plugins/MedievalRoleplayEngine/config.yml").exists())) {
            LocalConfigService.getInstance().saveMissingConfigDefaultsIfNotPresent();
        }
        else {
            // pre load compatibility checks
            if (isVersionMismatched()) {
                LocalConfigService.getInstance().saveMissingConfigDefaultsIfNotPresent();
            }
            reloadConfig();
        }
    }

    private void registerEventHandlers() {
        ArrayList<Listener> listeners = new ArrayList<>();
        listeners.add(new ChatHandler());
        listeners.add(new InteractionHandler());
        listeners.add(new JoinHandler());
        EventHandlerRegistry eventHandlerRegistry = new EventHandlerRegistry(getPonderAPI());
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
        getPonderAPI().getCommandService().initialize(commands, "That command wasn't found.");
    }
}