package dev.appeazethecheese.pvpmanager;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.Objects;
import java.util.TimerTask;

public class PvpState {
    private boolean enabled = true;
    private boolean forced = false;
    private boolean sendMessageOnCooldownEnd = false;
    private boolean leaveFlag = false;
    private boolean leaveCooldownActive = false;
    private CooldownTimer cooldown = new CooldownTimer();
    private final Player player;

    public PvpState(Player player){
        this.player = player;
    }
    public void SetEnabled(boolean enabled, boolean forced){
        this.enabled = enabled;
        if(!enabled)
            this.forced = forced;
        else
            this.forced = false;
    }

    public void RaiseLeaveFlag(){ leaveFlag = true; }

    public boolean CheckLeaveFlag(){
        if(leaveFlag){
            if(cooldown.isStarted())
                cooldown.cancel();
            cooldown = new CooldownTimer();
            cooldown.schedule(new TimerTask() {
                @Override
                public void run() {
                    leaveCooldownActive = false;
                }
            }, 120000L);
            leaveCooldownActive = true;
            leaveFlag = false;
            return true;
        }
        return false;
    }

    public void SetCooldownFinishMessage(){
        if(cooldown.isStarted())
            sendMessageOnCooldownEnd = true;
    }

    public void StartOrResetCooldown(){
        if(leaveCooldownActive)
            return;
        if(cooldown.isStarted())
            cooldown.cancel();
        cooldown = new CooldownTimer();
        cooldown.schedule(new TimerTask() {
            @Override
            public void run() {
                if(sendMessageOnCooldownEnd)
                    player.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "You can now use /pvp disable"));
            }
        }, 10000L);
    }

    public boolean IsEnabled(){
        return enabled;
    }

    public boolean IsForced(){
        return forced;
    }

    public boolean IsAdmin(){
        return Objects.requireNonNull(player.getPlayer()).hasPermission("pvpmanager.toggleothers");
    }

    public boolean IsCooldownActive() {
        if(Objects.requireNonNull(player.getPlayer()).hasPermission("pvpmanager.ignorecooldown"))
            return false;
        return cooldown.isStarted();
    }

    public boolean IsLeaveCooldownActive() {
        if(Objects.requireNonNull(player.getPlayer()).hasPermission("pvpmanager.ignorecooldown"))
            return false;
        return leaveCooldownActive;
    }
}
