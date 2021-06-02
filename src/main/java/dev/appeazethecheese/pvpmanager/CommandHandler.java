package dev.appeazethecheese.pvpmanager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.*;

import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;

public class CommandHandler {
    public static boolean pvpCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            // Console
            ConsoleCommandSender console = (ConsoleCommandSender) sender;
            if (args.length < 2) {
                console.sendMessage("Usage: pvp <on|off|toggle> <user>");
                return true;
            }

            Player target = Bukkit.getServer().getPlayer(args[1]);
            if(target == null) {
                console.sendMessage("Could not find user " + args[1]);
                return true;
            }
            PvpState state = PvpStateManager.GetState(target);
            switch(args[0].toLowerCase(Locale.ROOT)){
                case "on":
                    if(state.IsEnabled()){
                        console.sendMessage("PVP is already enabled for " + target.getName());
                        return true;
                    }
                    state.SetEnabled(true, true);
                    console.sendMessage("PVP has been enabled for " + target.getName());
                    target.sendMessage(ChatColor.GREEN + "A staff member has enabled PVP for you.");
                    break;
                case "off":
                    if(!state.IsEnabled()){
                        if(state.IsForced()){
                            console.sendMessage("PVP has already been force disabled for " + target.getName());
                            return true;
                        }
                        console.sendMessage(target.getName() + " already has PVP disabled. Setting it to forced.");
                        target.sendMessage(ChatColor.YELLOW + "A staff member has force disabled PVP for you.");
                        state.SetEnabled(false, true);
                        return true;
                    }
                    state.SetEnabled(false, true);
                    target.sendMessage(ChatColor.YELLOW + "A staff member has force disabled PVP for you.");
                    console.sendMessage("PVP has been disabled for " + target.getName());

                    break;
                case "toggle":
                    boolean enabled = !state.IsEnabled();
                    state.SetEnabled(enabled, true);

                    String consoleMsg = "PVP has been ";
                    consoleMsg += enabled ? "enabled" : "disabled";
                    consoleMsg += "for " + target.getName();

                    console.sendMessage(consoleMsg);

                    String targetMsg = enabled ? ChatColor.GREEN + "" : ChatColor.YELLOW + "";
                    targetMsg += "A staff member has ";
                    targetMsg += enabled ? "enabled" : "force disabled";
                    targetMsg += " PVP for you.";

                    target.sendMessage(targetMsg);
                    break;
                default:
                    console.sendMessage("Usage: pvp <on|off|toggle> <user>");
                    return true;
            }
        } else if (sender instanceof Player) {
            // Player
            Player player = (Player) sender;
            PvpState playerState = PvpStateManager.GetState(player);

            if(args.length <= 0){
                // No arguments
                if(playerState.IsAdmin())
                    player.sendMessage("Usage: /pvp <on|off|toggle> [user]");
                else
                    player.sendMessage("Usage: /pvp <on|off|toggle>");
                return true;
            }
            if(args.length > 1 && playerState.IsAdmin()){
                // Admin toggling someone else's pvp
                Player target = Bukkit.getServer().getPlayer(args[1]);
                if(target == null){
                    player.sendMessage(ChatColor.RED + "User " + ChatColor.UNDERLINE + args[1] +  ChatColor.RESET + "" + ChatColor.RED + " could not be found.");
                    return true;
                }
                PvpState state = PvpStateManager.GetState(target);
                switch(args[0].toLowerCase(Locale.ROOT)){
                    case "on":
                        if(state.IsEnabled()){
                            player.sendMessage(ChatColor.GREEN + "PVP is already enabled for " + target.getName());
                            return true;
                        }
                        state.SetEnabled(true, true);
                        player.sendMessage(ChatColor.GREEN + "PVP has been enabled for " + target.getName());
                        target.sendMessage(ChatColor.GREEN + "A staff member has enabled PVP for you.");
                        break;
                    case "off":
                        if(!state.IsEnabled()){
                            if(state.IsForced()){
                                player.sendMessage(ChatColor.YELLOW + "PVP has already been force disabled for " + target.getName());
                                return true;
                            }
                            state.SetEnabled(false, true);
                            player.sendMessage(ChatColor.YELLOW + target.getName() + " already has PVP disabled. Setting it to forced.");
                            target.sendMessage(ChatColor.YELLOW + "A staff member has force disabled PVP for you.");
                            return true;
                        }
                        state.SetEnabled(false, true);
                        player.sendMessage(ChatColor.YELLOW + "PVP has been disabled for " + target.getName());
                        target.sendMessage(ChatColor.YELLOW + "A staff member has force disabled PVP for you.");
                        break;
                    case "toggle":
                        boolean enabled = !state.IsEnabled();
                        state.SetEnabled(enabled, true);

                        String senderMsg = enabled ? ChatColor.GREEN + "" : ChatColor.YELLOW + "";
                        senderMsg += "PVP has been ";
                        senderMsg += enabled ? "enabled" : "disabled";
                        senderMsg += " for " + target.getName();

                        player.sendMessage(senderMsg);

                        String targetMsg = enabled ? ChatColor.GREEN + "" : ChatColor.YELLOW + "";
                        targetMsg += "A staff member has ";
                        targetMsg += enabled ? "enabled" : "force disabled";
                        targetMsg += " PVP for you.";

                        target.sendMessage(targetMsg);
                        break;
                    default:
                        player.sendMessage("Usage: pvp <on|off|toggle> [user]");
                        return true;
                }
            }
            else{
                // Toggling their own pvp
                if(playerState.IsCooldownActive()){
                    if(playerState.IsLeaveCooldownActive())
                        player.sendMessage(ChatColor.RED + "You are on a cooldown for leaving while in PVP. You must wait to use this command.");
                    else
                        player.sendMessage(ChatColor.RED + "You may not use this command while in PVP.");
                    playerState.SetCooldownFinishMessage();
                    return true;
                }

                boolean canChangeState = playerState.IsEnabled() || !playerState.IsForced() || playerState.IsAdmin();
                if(!canChangeState){
                    player.sendMessage(ChatColor.RED + "PVP was disabled for you by an admin. Please contact a staff member to get it re-enabled.");
                    return true;
                }

                switch(args[0].toLowerCase(Locale.ROOT)){
                    case "on":
                        if(playerState.IsEnabled()){
                            player.sendMessage(ChatColor.GREEN + "PVP is already enabled.");
                            break;
                        }
                        playerState.SetEnabled(true, false);
                        player.sendMessage(ChatColor.GREEN + "PVP enabled.");
                        break;
                    case "off":
                        if(!playerState.IsEnabled()){
                            player.sendMessage(ChatColor.YELLOW + "PVP is already disabled.");
                            break;
                        }
                        playerState.SetEnabled(false, false);
                        player.sendMessage(ChatColor.YELLOW + "PVP disabled.");
                        break;
                    case "toggle":
                        boolean enabled = !playerState.IsEnabled();
                        playerState.SetEnabled(enabled, false);
                        String msg = enabled ? ChatColor.GREEN + "" : ChatColor.YELLOW + "";
                        msg += "PVP ";
                        msg += enabled ? "enabled." : "disabled.";
                        player.sendMessage(msg);
                        break;
                    default:
                        player.sendMessage("Usage: pvp <on|off|toggle>");
                        return true;
                }
            }
        }
        return true;
    }
    public static List<String> pvpCommandTabFill(CommandSender sender, Command command, String alias, String[] args){
        ArrayList<String> ret = new ArrayList<>();
        if(args.length <= 1){
            ret.add("on");
            ret.add("off");
            ret.add("toggle");
        }
        else if(args.length <= 2 && (sender instanceof Player)){
            PvpState state = PvpStateManager.GetState(((Player) sender));
            if(state.IsAdmin()){
                ret.addAll(Bukkit.getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
            }
        }
        return ret;
    }
}
