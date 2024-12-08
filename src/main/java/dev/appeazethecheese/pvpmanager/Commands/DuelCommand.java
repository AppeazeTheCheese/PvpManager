package dev.appeazethecheese.pvpmanager.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DuelCommand implements CommandExecutor, TabCompleter {

    private final String usage = "Usage: /duel {player}";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player))
        {
            sender.sendMessage("You must be a player to use this command.");
            return true;
        }

        if(args.length <= 0){
            sender.sendMessage(usage);
            return true;
        }

        var arg = args[0];
        var target = Bukkit.getServer().getOnlinePlayers().stream().filter(x -> x.getName().equalsIgnoreCase(arg)).findFirst();

        if(target.isEmpty()){
            sender.sendMessage(ChatColor.RED + "Unknown player.");
            return true;
        }

        var targetPlayer = target.get();
        var distance = player.getLocation().distance(targetPlayer.getLocation());

        if(distance > 10){
            sender.sendMessage(ChatColor.RED + "You must be within 10 blocks of a player to duel them.");
            return true;
        }

        sender.sendMessage(ChatColor.GREEN + "Duel started.");
        targetPlayer.sendMessage(ChatColor.GREEN + "Duel started.");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
