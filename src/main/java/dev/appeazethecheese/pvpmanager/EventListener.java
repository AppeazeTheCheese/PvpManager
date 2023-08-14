package dev.appeazethecheese.pvpmanager;

import dev.appeazethecheese.pvpmanager.Cooldown.CooldownType;
import dev.appeazethecheese.pvpmanager.Data.PlayerDataManager;
import dev.appeazethecheese.pvpmanager.Data.PvpState;
import dev.appeazethecheese.pvpmanager.Data.PvpStateManager;
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

        PvpState attackerState = PvpStateManager.getOrCreateState(attacker);
        PvpState targetState = PvpStateManager.getOrCreateState(target);
        if (!attackerState.isEnabled()) {
            attacker.sendMessage(ChatColor.RED + "You can't attack others when you have PVP disabled. To enable, do /pvp on");
            event.setCancelled(true);
            return;
        }
        if (!targetState.isEnabled()) {
            attacker.sendMessage(ChatColor.RED + target.getDisplayName() + " has PVP disabled.");
            event.setCancelled(true);
            return;
        }

        attackerState.lockStateWithCooldown(CooldownType.Pvp, 10);
        targetState.lockStateWithCooldown(CooldownType.Pvp, 10);
    }
    @EventHandler
    public void onPotionSplash(PotionSplashEvent event){
        if(!(event.getPotion().getShooter() instanceof Player)) return;
        if(event.getPotion().getEffects().stream().noneMatch(x -> x.getType().getName().equals(PotionEffectType.POISON.getName()))) return;

        Player attacker = (Player) event.getPotion().getShooter();
        PvpState attackerState = PvpStateManager.getOrCreateState(attacker);
        for(LivingEntity e : event.getAffectedEntities().stream().filter(x -> x instanceof Player).collect(Collectors.toList())){
            if(e.getUniqueId() == attacker.getUniqueId())
                continue;

            Player target = (Player)e;
            PvpState targetState = PvpStateManager.getOrCreateState(target);
            if(!attackerState.isEnabled()){
                attacker.sendMessage(ChatColor.RED + "You can't attack others when you have PVP disabled. To enable, do /pvp on");
                event.setIntensity(e, 0);
            }
            else if(!targetState.isEnabled()){
                attacker.sendMessage(ChatColor.RED + target.getDisplayName() + " has PVP disabled.");
                event.setIntensity(e, 0);
            }
        }
    }
    @EventHandler public void onPlayerLeave(PlayerQuitEvent event){
        PvpState state = PvpStateManager.getOrCreateState(event.getPlayer());
        state.playerLeft();

        PvpStateManager.flush(event.getPlayer().getUniqueId());
    }

    @EventHandler public void onPlayerJoin(PlayerJoinEvent event){
        PvpState state = PvpStateManager.getOrCreateState(event.getPlayer());
        state.playerJoined();
    }
}
