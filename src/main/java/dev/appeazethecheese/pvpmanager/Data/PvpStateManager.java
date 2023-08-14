package dev.appeazethecheese.pvpmanager.Data;

import dev.appeazethecheese.pvpmanager.Cooldown.CooldownType;
import dev.appeazethecheese.pvpmanager.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.*;

public class PvpStateManager {
    private static final Map<UUID, PvpState> playerStates = new HashMap<>();

    public static PvpState getOrCreateState(Player player){
        if(!playerStates.containsKey(player.getUniqueId())){
            var state = new PvpState(player);
            var data = PlayerDataManager.getPlayerData(player);

            if(data.locked){
                state.setLockedState(data.pvpEnabled);
                if(data.cooldownCause != CooldownType.Inactive && data.cooldownSecondsRemaining > 0){
                    state.lockStateWithCooldown(data.cooldownCause, data.cooldownSecondsRemaining);
                }
            }
            else{
                state.setUnlockedState(data.pvpEnabled);
            }

            playerStates.put(player.getUniqueId(), state);
            return state;
        }
        return playerStates.get(player.getUniqueId());
    }

    public static void createStatesForOnlinePlayers(){
        for(Player player : Bukkit.getOnlinePlayers()){
            getOrCreateState(player);
        }
    }

    public static PvpState getState(UUID uuid){
        if(!playerStates.containsKey(uuid))
            return null;
        return playerStates.get(uuid);
    }

    public static void flush(UUID uuid) {
        if(!playerStates.containsKey(uuid)) return;

        var state = playerStates.get(uuid);
        try {
            PlayerDataManager.savePlayerData(uuid, state);
        } catch (IOException e) {
            Main.Instance.getLogger().warning("Failed to save data for player with UUID " + uuid + ":\n" + e);
        }

        playerStates.remove(uuid);
    }
}
