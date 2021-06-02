package dev.appeazethecheese.pvpmanager;

import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.stream.Collectors;

public class EventListener implements Listener {
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        Projectile projectile = null;
        TNTPrimed tnt = null;

        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Player)) {
            if(event.getDamager() instanceof Projectile){
                projectile = (Projectile) event.getDamager();
                if(!(projectile.getShooter() instanceof Player))
                    return;
            }
            else if(event.getDamager() instanceof TNTPrimed)
            {
                tnt = (TNTPrimed) event.getDamager();
                if(!(tnt.getSource() instanceof Player))
                    return;
            }
            else
                return;
        }

        // PVP
        Player attacker = null;
        if(tnt != null)
            attacker = (Player) tnt.getSource();
        else if(projectile != null)
            attacker = (Player) projectile.getShooter();
        else
            attacker = (Player) event.getDamager();

        Player target = (Player) event.getEntity();

        if (attacker.getUniqueId() == target.getUniqueId()) {
            // Player damaging themselves
            return;
        }

        PvpState attackerState = PvpStateManager.GetState(attacker);
        PvpState targetState = PvpStateManager.GetState(target);
        if (!attackerState.IsEnabled()) {
            attacker.sendMessage(ChatColor.RED + "You can't attack others when you have PVP disabled.");
            event.setCancelled(true);
            return;
        }
        if (!targetState.IsEnabled()) {
            attacker.sendMessage(ChatColor.RED + target.getDisplayName() + " has PVP disabled.");
            event.setCancelled(true);
            return;
        }

        attackerState.StartOrResetCooldown();
        targetState.StartOrResetCooldown();
    }
    @EventHandler
    public void onPotionSplash(PotionSplashEvent event){
        if(!(event.getPotion().getShooter() instanceof Player)) return;
        if(event.getPotion().getEffects().stream().noneMatch(x -> x.getType().getName().equals(PotionEffectType.POISON.getName()))) return;

        Player attacker = (Player) event.getPotion().getShooter();
        PvpState attackerState = PvpStateManager.GetState(attacker);
        for(LivingEntity e : event.getAffectedEntities().stream().filter(x -> x instanceof Player).collect(Collectors.toList())){
            if(e.getUniqueId() == attacker.getUniqueId())
                continue;

            Player target = (Player)e;
            PvpState targetState = PvpStateManager.GetState(target);
            if(!attackerState.IsEnabled()){
                attacker.sendMessage(ChatColor.RED + "You can't attack others when you have PVP disabled.");
                event.setIntensity(e, 0);
            }
            else if(!targetState.IsEnabled()){
                attacker.sendMessage(ChatColor.RED + target.getDisplayName() + " has PVP disabled.");
                event.setIntensity(e, 0);
            }
        }
    }
    @EventHandler public void onPlayerLeave(PlayerQuitEvent event){
        PvpState state = PvpStateManager.GetState(event.getPlayer());
        if(state.IsCooldownActive() && !state.IsLeaveCooldownActive())
            state.RaiseLeaveFlag();
    }

    @EventHandler public void onPlayerJoin(PlayerJoinEvent event){
        PvpState state = PvpStateManager.GetState(event.getPlayer());
        state.CheckLeaveFlag();
    }
}
