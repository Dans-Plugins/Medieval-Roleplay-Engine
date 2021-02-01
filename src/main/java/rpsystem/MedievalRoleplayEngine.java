package rpsystem;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import rpsystem.EventHandlers.AsyncPlayerChatEventHandler;
import rpsystem.EventHandlers.PlayerInteractAtEntityEventHandler;
import rpsystem.EventHandlers.PlayerJoinEventHandler;
import rpsystem.Objects.CharacterCard;
import rpsystem.bStats.Metrics;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class MedievalRoleplayEngine extends JavaPlugin {

    private static MedievalRoleplayEngine instance;

    // version
    private String version = "v1.6-alpha-1";

    // temporary
    public ArrayList<UUID> playersWithBusyBirds = new ArrayList<>();
    public ArrayList<UUID> playersSpeakingInLocalChat = new ArrayList<>();
    public ArrayList<UUID> playersOnNameChangeCooldown = new ArrayList<>();
    public ArrayList<UUID> playersWithRightClickCooldown = new ArrayList<>();

    public static MedievalRoleplayEngine getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        System.out.println("Medieval Roleplay Engine plugin enabling....");

        instance = this;

        EventRegistry.getInstance().registerEvents();

        if (StorageManager.getInstance().oldSaveFolderPresent()) {
            StorageManager.getInstance().legacyLoadCards();
            StorageManager.getInstance().deleteLegacyFiles(new File("./plugins/medieval-roleplay-engine/"));
            StorageManager.getInstance().saveCardFileNames();
            StorageManager.getInstance().saveCards();
        }
        else {
            StorageManager.getInstance().loadCards();
        }

        int pluginId = 8996;

        Metrics metrics = new Metrics(this, pluginId);

        System.out.println("Medieval Roleplay Engine plugin enabled.");
    }

    @Override
    public void onDisable() {
        System.out.println("Medieval Roleplay Engine plugin disabling....");
        StorageManager.getInstance().saveCardFileNames();
        StorageManager.getInstance().saveCards();
        System.out.println("Medieval Roleplay Engine plugin disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return CommandInterpreter.getInstance().interpretCommand(sender, label, args);
    }

    public String getVersion() {
        return version;
    }

}