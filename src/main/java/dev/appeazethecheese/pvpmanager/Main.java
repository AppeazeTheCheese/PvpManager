package dev.appeazethecheese.pvpmanager;

import dev.appeazethecheese.pvpmanager.Commands.CommandHandler;
import dev.appeazethecheese.pvpmanager.Data.PlayerData;
import dev.appeazethecheese.pvpmanager.Data.PlayerDataManager;
import dev.appeazethecheese.pvpmanager.Data.PvpStateManager;
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
        Objects.requireNonNull(getCommand("pvp")).setExecutor(CommandHandler::pvpCommand);
        Objects.requireNonNull(getCommand("pvp")).setTabCompleter(CommandHandler::pvpCommandTabFill);
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
