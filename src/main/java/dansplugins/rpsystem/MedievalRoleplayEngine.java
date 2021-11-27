package dansplugins.rpsystem;

import dansplugins.rpsystem.bstats.Metrics;
import dansplugins.rpsystem.commands.*;
import dansplugins.rpsystem.eventhandlers.ChatHandler;
import dansplugins.rpsystem.eventhandlers.InteractionHandler;
import dansplugins.rpsystem.eventhandlers.JoinHandler;
import dansplugins.rpsystem.managers.StorageManager;
import dansplugins.rpsystem.placeholders.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import preponderous.ponder.AbstractPonderPlugin;
import preponderous.ponder.misc.PonderAPI_Integrator;
import preponderous.ponder.misc.specification.ICommand;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MedievalRoleplayEngine extends AbstractPonderPlugin {
    private static MedievalRoleplayEngine instance;
    private String version = "v2.0-alpha-1";

    public static MedievalRoleplayEngine getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        int pluginId = 8996;
        Metrics metrics = new Metrics(this, pluginId);

        ponderAPI_integrator = new PonderAPI_Integrator(this);
        toolbox = getPonderAPI().getToolbox();
        initializeConfigService();
        initializeConfigFile();
        registerEventHandlers();
        initializeCommandService();
        getPonderAPI().setDebug(false);

        StorageManager.getInstance().load();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPI().register();
        } else {
            if (isDebugEnabled()) { System.out.println("Couldn't find PlaceholderAPI, no placeholders will be available."); }
        }
    }

    @Override
    public void onDisable() {
        StorageManager.getInstance().save();
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
        configOptions.put("version", getVersion());
        configOptions.put("debugMode", false);
        configOptions.put("localChatRadius", 25);
        configOptions.put("whisperChatRadius", 2);
        configOptions.put("yellChatRadius", 50);
        configOptions.put("emoteRadius", 25);
        configOptions.put("changeNameCooldown", 300);
        configOptions.put("localChatColor", "gray");
        configOptions.put("whisperChatColor", "blue");
        configOptions.put("yellChatColor", "red");
        configOptions.put("emoteColor", "gray");
        configOptions.put("rightClickToViewCard", true);
        configOptions.put("localOOCChatRadius", 25);
        configOptions.put("localOOCChatColor", "gray");
        configOptions.put("positiveAlertColor", "green");
        configOptions.put("neutralAlertColor", "aqua");
        configOptions.put("negativeAlertColor", "red");
        configOptions.put("chatFeaturesEnabled", true);
        configOptions.put("legacyChat", false);
        configOptions.put("preventSelfBirding", true);
        getPonderAPI().getConfigService().initialize(configOptions);
    }

    private void initializeConfigFile() {
        if (!(new File("./plugins/MedievalRoleplayEngine/config.yml").exists())) {
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