package dansplugins.rpsystem;

import dansplugins.rpsystem.placeholders.PlaceholderAPI;
import dansplugins.rpsystem.bstats.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public class MedievalRoleplayEngine extends JavaPlugin {

    private static MedievalRoleplayEngine instance;

    // version
    private String version = "v1.6.1";

    public static MedievalRoleplayEngine getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        System.out.println("Medieval Roleplay Engine plugin enabling....");

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
            System.out.println("Couldn't find PlaceholderAPI, no placeholders will be available.");
        }

        System.out.println("Medieval Roleplay Engine plugin enabled.");
    }

    @Override
    public void onDisable() {
        System.out.println("Medieval Roleplay Engine plugin disabling....");
        StorageManager.getInstance().saveCardFileNames();
        StorageManager.getInstance().saveCards();
        if (ConfigManager.getInstance().hasBeenAltered()) {
            saveConfig();
        }
        System.out.println("Medieval Roleplay Engine plugin disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return CommandInterpreter.getInstance().interpretCommand(sender, label, args);
    }

    public String getVersion() {
        return version;
    }

    public boolean isVersionMismatched() {
        return !getConfig().getString("version").equalsIgnoreCase(getVersion());
    }

}