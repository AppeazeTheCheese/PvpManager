package dev.appeazethecheese.pvpmanager.Data;

import dev.appeazethecheese.pvpmanager.Cooldown.CooldownTimer;
import dev.appeazethecheese.pvpmanager.Cooldown.CooldownType;
import dev.appeazethecheese.pvpmanager.Main;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerData {
    public boolean pvpEnabled;
    public boolean locked;
    public CooldownType cooldownCause;
    public int cooldownSecondsRemaining;

    public PlayerData(){}

    public PlayerData(PlayerData other){
        this.pvpEnabled = other.pvpEnabled;
        this.locked = other.locked;
        this.cooldownCause = other.cooldownCause;
        this.cooldownSecondsRemaining = other.cooldownSecondsRemaining;
    }

    public Map<String, Object> toConfigValues(){
        var data = new HashMap<String, Object>();

        data.put("pvpEnabled", pvpEnabled);
        data.put("locked", locked);
        data.put("cooldownCause", cooldownCause.name());
        data.put("cooldownSecondsRemaining", cooldownSecondsRemaining);

        return data;
    }

    public static PlayerData fromConfigValues(Map<?, ?> values){
        var data = new PlayerData(){
            {
                pvpEnabled = (Boolean) values.get("pvpEnabled");
                locked = (Boolean) values.get("locked");
                cooldownCause = CooldownType.valueOf((String) values.get("cooldownCause"));
                cooldownSecondsRemaining = (Integer) values.get("cooldownSecondsRemaining");
            }
        };

        return data;
    }
}
