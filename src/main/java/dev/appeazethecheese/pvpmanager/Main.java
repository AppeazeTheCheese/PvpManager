package dev.appeazethecheese.pvpmanager;

import dev.appeazethecheese.pvpmanager.Commands.DuelCommand;
import dev.appeazethecheese.pvpmanager.Commands.PvpCommand;
import dev.appeazethecheese.pvpmanager.Data.PlayerDataManager;
import dev.appeazethecheese.pvpmanager.States.PvpStateManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Objects;

public class Main extends JavaPlugin {

    public static Main Instance;

    public Main(){
        Instance = this;
    }

    @Override
    public void onEnable() {
        var pvpCommand = new PvpCommand();
        var duelCommand = new DuelCommand();

        getCommand("pvp").setExecutor(pvpCommand);
        getCommand("pvp").setTabCompleter(pvpCommand);

        getCommand("duel").setExecutor(duelCommand);
        getCommand("duel").setTabCompleter(duelCommand);

        getServer().getPluginManager().registerEvents(new EventListener(), this);

        saveDefaultConfig();
        saveResource("playerdata.yml", false);

        PlayerDataManager.loadConfig();
        PvpStateManager.createStatesForOnlinePlayers();
    }

    @Override
    public void onDisable() {
        try {
            PlayerDataManager.saveOnlinePlayerData();
        } catch (IOException e) {
            getLogger().warning("Failed to save player data:\n" + e);
        }
    }
}
