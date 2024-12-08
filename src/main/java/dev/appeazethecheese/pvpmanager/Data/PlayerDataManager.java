package dev.appeazethecheese.pvpmanager.Data;

import dev.appeazethecheese.pvpmanager.Main;
import dev.appeazethecheese.pvpmanager.States.PvpState;
import dev.appeazethecheese.pvpmanager.States.PvpStateManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private static YamlConfiguration playerData;

    private static final File dataFile = new File(Main.Instance.getDataFolder(), "playerdata.yml");

    public static void loadConfig(){
        playerData = YamlConfiguration.loadConfiguration(dataFile);
    }

    public static PlayerData getPlayerData(Player player){
        return getPlayerData(player.getUniqueId());
    }

    public static PlayerData getPlayerData(UUID playerId){
        var section = playerData.get(String.valueOf(playerId));
        if(section != null){
            Map<?, ?> values;
            if(section instanceof ConfigurationSection){
                values = ((ConfigurationSection) section).getValues(false);
            }
            else{
                values = ((Map<?, ?>) section);
            }

            return PlayerData.fromConfigValues(values);
        }
        var defaultSection = Main.Instance.getConfig().getConfigurationSection("defaultSettings");
        var data = PlayerData.fromConfigValues(defaultSection.getValues(false));

        playerData.set(String.valueOf(playerId), data.toConfigValues());
        try {
            savePlayerData(playerId, null);
        }catch (Throwable e){
            Main.Instance.getLogger().warning("Failed to save data for new player with UUID " + playerId + ":\n" + e);
        }
        return data;
    }


    public static void savePlayerData(Player player, PvpState state) throws IOException {
        savePlayerData(player.getUniqueId(), state);
    }

    public static void savePlayerData(UUID playerId, PvpState state) throws IOException {
        setPlayerData(playerId, state);
        saveFile();
    }

    public static void saveOnlinePlayerData() throws IOException {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            setPlayerData(player.getUniqueId(), PvpStateManager.getState(player.getUniqueId()));
        }

        saveFile();
    }

    private static void setPlayerData(UUID playerId, PvpState state){
        if(state == null){
            return;
        }

        var data = new PlayerData(){
            {
                pvpEnabled = state.isEnabled();
                locked = state.isLocked();
                cooldownCause = state.getCooldownCause();
                cooldownSecondsRemaining = state.getCooldownSecondsRemaining();
            }
        };

        playerData.set(String.valueOf(playerId), data.toConfigValues());
    }

    private static void saveFile() throws IOException {
        playerData.save(dataFile);
    }
}
