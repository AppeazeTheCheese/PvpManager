package dev.appeazethecheese.pvpmanager.Commands;

import dev.appeazethecheese.pvpmanager.Cooldown.CooldownType;
import dev.appeazethecheese.pvpmanager.States.PvpState;
import dev.appeazethecheese.pvpmanager.States.PvpStateManager;
import dev.appeazethecheese.pvpmanager.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PvpCommand implements CommandExecutor, TabCompleter {
    private static final Map<Character, Integer> aliasMultiplierMap = new HashMap<>(){{
        put('s', 1);
        put('m', 60);
        put('h', 60 * 60);
        put('d', 60 * 60 * 24);
    }};

    private static final String adminUsage =
            "Usage:\n" +
            "   /pvp <on|off>\n" +
            "   /pvp on <player>\n" +
            "   /pvp off <player> [timeout]\n";

    public static final String basicUsage = "Usage: /pvp <on|off>";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            // Console
            ConsoleCommandSender console = (ConsoleCommandSender) sender;
            if (args.length < 2) {
                console.sendMessage(adminUsage);
                return true;
            }

            Player target = Bukkit.getServer().getPlayer(args[1]);
            if(target == null) {
                console.sendMessage("Could not find user " + args[1]);
                return true;
            }
            PvpState state = PvpStateManager.getOrCreateState(target);
            switch(args[0].toLowerCase(Locale.ROOT)){
                case "on":
                    if(state.isEnabled()){
                        console.sendMessage("PVP is already enabled for " + target.getName());
                        return true;
                    }
                    state.setUnlockedState(true);
                    console.sendMessage("PVP has been enabled for " + target.getName());
                    target.sendMessage(ChatColor.GREEN + "A staff member has enabled PVP for you.");
                    break;
                case "off":
                    if(args.length >= 3){
                        var arg = args[2];
                        var pattern = Pattern.compile("^\\d+[smhd]$", Pattern.CASE_INSENSITIVE);
                        var valid = pattern.matcher(arg).find();

                        if(!valid){
                            sender.sendMessage("The provided timeout is in an invalid format.");
                            return true;
                        }

                        var timePart = arg.substring(0, arg.length() - 1);
                        var unitPart = arg.substring(arg.length() - 1);

                        var time = Integer.parseInt(timePart);
                        var unit = unitPart.toLowerCase().charAt(0);
                        var seconds = time * aliasMultiplierMap.get(unit);

                        state.setLockedState(false);
                        state.lockStateWithCooldown(CooldownType.Admin, seconds);
                        target.sendMessage(ChatColor.YELLOW + "A staff member has force disabled PVP for you for " + Util.FormatTimeout(seconds));
                        console.sendMessage("PVP has been disabled for " + target.getName() + " for " + Util.FormatTimeout(seconds));
                    }
                    else{
                        if(!state.isEnabled()){
                            if(state.isLocked() && state.getCooldownCause() == CooldownType.Inactive){
                                console.sendMessage("PVP has already been force disabled for " + target.getName());
                                return true;
                            }
                        }
                        state.setLockedState(false);
                        target.sendMessage(ChatColor.YELLOW + "A staff member has force disabled PVP for you.");
                        console.sendMessage("PVP has been disabled for " + target.getName());
                    }

                    break;
                default:
                    console.sendMessage(basicUsage);
                    return true;
            }
        } else if (sender instanceof Player) {
            // Player
            Player player = (Player) sender;
            PvpState playerState = PvpStateManager.getOrCreateState(player);

            if(args.length <= 0){
                // No arguments
                if(playerState.canToggleOthers())
                    player.sendMessage(adminUsage);
                else
                    player.sendMessage(basicUsage);
                return true;
            }
            if(args.length > 1 && playerState.canToggleOthers()){
                // Admin toggling someone else's pvp
                Player target = Bukkit.getServer().getPlayer(args[1]);
                if(target == null){
                    player.sendMessage(ChatColor.RED + "User " + ChatColor.UNDERLINE + args[1] +  ChatColor.RESET + "" + ChatColor.RED + " could not be found.");
                    return true;
                }
                PvpState state = PvpStateManager.getOrCreateState(target);
                switch(args[0].toLowerCase(Locale.ROOT)){
                    case "on":
                        if(state.isEnabled()){
                            player.sendMessage(ChatColor.GREEN + "PVP is already enabled for " + target.getName());
                            return true;
                        }
                        state.setUnlockedState(true);
                        player.sendMessage(ChatColor.GREEN + "PVP has been enabled for " + target.getName());
                        target.sendMessage(ChatColor.GREEN + "A staff member has enabled PVP for you.");
                        break;
                    case "off":
                        if(args.length >= 3){
                            var arg = args[2];
                            var pattern = Pattern.compile("^\\d+[smhd]$", Pattern.CASE_INSENSITIVE);
                            var valid = pattern.matcher(arg).find();

                            if(!valid){
                                sender.sendMessage(ChatColor.RED + "The provided timeout is in an invalid format.");
                                return true;
                            }

                            var timePart = arg.substring(0, arg.length() - 1);
                            var unitPart = arg.substring(arg.length() - 1);

                            var time = Integer.parseInt(timePart);
                            var unit = unitPart.toLowerCase().charAt(0);
                            var seconds = time * aliasMultiplierMap.get(unit);

                            state.setLockedState(false);
                            state.lockStateWithCooldown(CooldownType.Admin, seconds);
                            player.sendMessage(ChatColor.YELLOW + "PVP has been disabled for " + target.getName() + " for " + Util.FormatTimeout(seconds));
                            target.sendMessage(ChatColor.YELLOW + "A staff member has force disabled PVP for you " + " for " + Util.FormatTimeout(seconds));
                        }
                        else{
                            if(!state.isEnabled()){
                                if(state.isLocked()){
                                    player.sendMessage(ChatColor.YELLOW + "PVP has already been force disabled for " + target.getName());
                                    return true;
                                }
                                state.setLockedState(false);
                                player.sendMessage(ChatColor.YELLOW + target.getName() + " already has PVP disabled. Setting it to forced.");
                                target.sendMessage(ChatColor.YELLOW + "A staff member has force disabled PVP for you.");
                                return true;
                            }
                            state.setLockedState(false);
                            player.sendMessage(ChatColor.YELLOW + "PVP has been disabled for " + target.getName());
                            target.sendMessage(ChatColor.YELLOW + "A staff member has force disabled PVP for you.");
                        }
                        break;
                    default:
                        player.sendMessage(adminUsage);
                        return true;
                }
            }
            else{
                // Toggling their own pvp
                if(playerState.getCooldownCause() != CooldownType.Inactive && !playerState.canBypassCooldowns()){
                    if(playerState.getCooldownCause() == CooldownType.Leave)
                        player.sendMessage(ChatColor.RED + "You are on a cooldown for leaving while in PVP. You can use this command in " + Util.FormatTimeout(playerState.getCooldownSecondsRemaining()));
                    else if(playerState.getCooldownCause() == CooldownType.Admin)
                        player.sendMessage(ChatColor.RED + "PVP was disabled for you by an admin. You can use this command in " + Util.FormatTimeout(playerState.getCooldownSecondsRemaining()));
                    else
                        player.sendMessage(ChatColor.RED + "You may not use this command while in PVP.");

                    playerState.setCooldownFinishMessage();
                    return true;
                }

                if(!playerState.canChangeState()){
                    player.sendMessage(ChatColor.RED + "PVP was disabled for you by an admin. Please contact a staff member to get it re-enabled.");
                    return true;
                }

                switch(args[0].toLowerCase(Locale.ROOT)){
                    case "on":
                        if(playerState.isEnabled()){
                            player.sendMessage(ChatColor.GREEN + "PVP is already enabled.");
                            break;
                        }
                        playerState.setUnlockedState(true);
                        player.sendMessage(ChatColor.GREEN + "PVP enabled.");
                        break;
                    case "off":
                        if(!playerState.isEnabled()){
                            player.sendMessage(ChatColor.YELLOW + "PVP is already disabled.");
                            break;
                        }
                        playerState.setUnlockedState(false);
                        player.sendMessage(ChatColor.YELLOW + "PVP disabled.");
                        break;
                    default:
                        player.sendMessage(basicUsage);
                        return true;
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args){
        ArrayList<String> ret = new ArrayList<>();
        if(args.length <= 1){
            ret.add("on");
            ret.add("off");
        }
        else if(args.length <= 2 && (sender instanceof Player)){
            PvpState state = PvpStateManager.getOrCreateState(((Player) sender));
            if(state.canToggleOthers()){
                ret.addAll(Bukkit.getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
            }
        }
        else if(args.length <= 3){
            PvpState state = PvpStateManager.getOrCreateState((Player) sender);
            if(state.canToggleOthers()){
                var input = args[2];
                if(input.length() <= 0){
                    var numbers = IntStream.rangeClosed(1, 9)
                            .boxed().collect(Collectors.toList())
                            .stream().map(String::valueOf).collect(Collectors.toList());

                    ret.addAll(numbers);
                }
                else{
                    try{
                        Integer.parseInt(input);
                    }catch (NumberFormatException e){
                        return ret;
                    }

                    for(var key : aliasMultiplierMap.keySet()){
                        ret.add(input + key);
                    }
                }
            }
        }
        return ret;
    }
}
