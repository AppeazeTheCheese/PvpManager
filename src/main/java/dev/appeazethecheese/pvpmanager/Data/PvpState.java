package dev.appeazethecheese.pvpmanager.Data;

import dev.appeazethecheese.pvpmanager.Cooldown.CooldownTimer;
import dev.appeazethecheese.pvpmanager.Cooldown.CooldownType;
import dev.appeazethecheese.pvpmanager.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.TimerTask;

public class PvpState {
    private boolean enabled = true;
    private boolean locked = false;
    private boolean sendMessageOnCooldownEnd = false;
    private CooldownType cooldownCause = CooldownType.Inactive;
    private CooldownTimer cooldown = null;
    private Player player;

    public PvpState(Player player){
        this.player = player;
    }

    public void setUnlockedState(boolean enabled){
        this.locked = false;
        this.enabled = enabled;

        if(cooldown != null && cooldown.isStarted())
        {
            cooldown.stop();
            cooldown = null;
        }
    }

    public void setLockedState(boolean enabled){
        this.locked = true;
        this.enabled = enabled;

        if(cooldown != null && cooldown.isStarted())
        {
            cooldown.stop();
            cooldown = null;
        }
    }

    public void playerLeft(){
        final int leavePenaltySeconds = 120;

        if(cooldownCause == CooldownType.Pvp){
            // Apply leave penalty

            var timer = new CooldownTimer(new TimerTask() {
                @Override
                public void run() {
                    onTimerElapsed();
                }
            }, leavePenaltySeconds);
            setLockedState(false);
            setCooldown(timer, CooldownType.Leave);
        }

        if(cooldown != null && cooldown.isStarted()){
            cooldown.stop();
        }
    }

    public void playerJoined(){

    }

    public void lockStateWithCooldown(CooldownType type, int seconds){
        if(cooldown != null && cooldown.isStarted()){
            if(type == CooldownType.Admin || cooldown.getSecondsRemaining() < seconds) cooldown.stop();
            else return;
        }

        var timer = new CooldownTimer(new TimerTask() {
            @Override
            public void run() {
                onTimerElapsed();
            }
        }, seconds);

        setCooldown(timer, type);
        locked = true;

        if(cooldown != null){
            cooldown.start();
        }
    }

    public void setCooldownFinishMessage(){
        if(cooldown != null && cooldown.isStarted())
            sendMessageOnCooldownEnd = true;
    }

    private void setCooldown(CooldownTimer timer, CooldownType type) {
        if(cooldown != null && cooldown.isStarted())
            cooldown.stop();

        if(canBypassCooldowns()) {
            cooldown = null;
            cooldownCause = CooldownType.Inactive;
            return;
        }

        cooldown = timer;
        cooldownCause = type;
    }

    private void onTimerElapsed(){
        if(sendMessageOnCooldownEnd)
            player.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "You can now change your PVP status using /pvp"));
        cooldownCause = CooldownType.Inactive;
        locked = false;
    }

    public CooldownType getCooldownCause(){
        return cooldownCause;
    }

    public int getCooldownSecondsRemaining(){
        if(cooldown == null) return 0;
        return cooldown.getSecondsRemaining();
    }

    public boolean isEnabled(){
        return enabled;
    }

    public boolean isLocked() { return locked; }

    public boolean canChangeState(){ return !locked || canToggleOthers(); }

    public boolean canToggleOthers(){
        return Objects.requireNonNull(player.getPlayer()).hasPermission("pvpmanager.admin.toggleothers");
    }

    public boolean canBypassCooldowns(){
        return Objects.requireNonNull(player.getPlayer()).hasPermission("pvpmanager.admin.ignorecooldown");
    }
}
