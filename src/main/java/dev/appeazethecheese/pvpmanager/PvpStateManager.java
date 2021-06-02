package dev.appeazethecheese.pvpmanager;

import org.bukkit.entity.Player;

import java.util.*;

public class PvpStateManager {
    private static final Map<UUID, PvpState> playerStates = new HashMap<>();

    public static PvpState GetState(Player player){
        if(!playerStates.containsKey(player.getUniqueId()))
            playerStates.put(player.getUniqueId(), new PvpState(player));
        return playerStates.get(player.getUniqueId());
    }
}
