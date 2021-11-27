package dansplugins.rpsystem;

import dansplugins.rpsystem.bstats.Metrics;
import dansplugins.rpsystem.commands.*;
import dansplugins.rpsystem.eventhandlers.ChatHandler;
import dansplugins.rpsystem.eventhandlers.InteractionHandler;
import dansplugins.rpsystem.eventhandlers.JoinHandler;
import dansplugins.rpsystem.managers.ConfigManager;
import dansplugins.rpsystem.managers.StorageManager;
import dansplugins.rpsystem.placeholders.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import preponderous.ponder.AbstractPonderPlugin;
import preponderous.ponder.misc.specification.ICommand;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MedievalRoleplayEngine extends AbstractPonderPlugin {

    private static MedievalRoleplayEngine instance;

    // version
    private String version = "v2.0-alpha-1";

    public static MedievalRoleplayEngine getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        // create/load config
        if (!(new File("./plugins/MedievalRoleplayEngine/config.yml").exists())) {
            ConfigManager.getInstance().saveConfigDefaults();
        }
        else {
            // pre load compatibility checks
            if (isVersionMismatched()) {
                ConfigManager.getInstance().handleVersionMismatch();
            }
            reloadConfig();
        }

        if (StorageManager.getInstance().oldSaveFolderPresent()) {
            StorageManager.getInstance().legacyLoadCards();
            StorageManager.getInstance().deleteLegacyFiles(new File("./plugins/medieval-roleplay-engine/"));
            StorageManager.getInstance().saveCardFileNames();
            StorageManager.getInstance().saveCards();
        }
        else {
            StorageManager.getInstance().loadCards();
        }

        EventRegistry.getInstance().registerEvents();

        int pluginId = 8996;

        Metrics metrics = new Metrics(this, pluginId);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPI().register();
        } else {
            if (MedievalRoleplayEngine.getInstance().isDebugEnabled()) { System.out.println("Couldn't find PlaceholderAPI, no placeholders will be available."); }
        }
    }

    @Override
    public void onDisable() {
        StorageManager.getInstance().saveCardFileNames();
        StorageManager.getInstance().saveCards();
        if (ConfigManager.getInstance().hasBeenAltered()) {
            saveConfig();
        }
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

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean isVersionMismatched() {
        String configVersion = this.getConfig().getString("version");
        if (configVersion == null || this.getVersion() == null) {
            return false;
        } else {
            return !configVersion.equalsIgnoreCase(this.getVersion());
        }
    }

    private void initializeConfigService() {
        HashMap<String, Object> configOptions = new HashMap<>();
        configOptions.put("debugMode", false);
        getPonderAPI().getConfigService().initialize(configOptions);
    }

    private void initializeConfigFile() {
        if (!(new File("./plugins/ModAssist/config.yml").exists())) {
            getPonderAPI().getConfigService().saveMissingConfigDefaultsIfNotPresent();
        }
        else {
            // pre load compatibility checks
            if (isVersionMismatched()) {
                getPonderAPI().getConfigService().saveMissingConfigDefaultsIfNotPresent();
            }
            reloadConfig();
        }
    }

    private void registerEventHandlers() {
        ArrayList<Listener> listeners = new ArrayList<>();
        listeners.add(new ChatHandler());
        listeners.add(new InteractionHandler());
        listeners.add(new JoinHandler());
        getToolbox().getEventHandlerRegistry().registerEventHandlers(listeners, this);
    }

    private void initializeCommandService() {
        ArrayList<ICommand> commands = new ArrayList<ICommand>(Arrays.asList(
                new BirdCommand(), new CardCommand(), new ConfigCommand(),
                new EmoteCommand(), new GlobalChatCommand(), new HelpCommand(),
                new LocalChatCommand(), new LocalOOCChatCommand(), new RollCommand(),
                new TitleCommand(), new WhisperCommand(), new YellCommand()
        ));
        getPonderAPI().getCommandService().initialize(commands, "That command wasn't found.");
    }

}