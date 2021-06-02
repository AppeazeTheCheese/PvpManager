package dev.appeazethecheese.pvpmanager;

import org.bukkit.command.Command;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        Objects.requireNonNull(getCommand("pvp")).setExecutor(CommandHandler::pvpCommand);
        Objects.requireNonNull(getCommand("pvp")).setTabCompleter(CommandHandler::pvpCommandTabFill);
        getServer().getPluginManager().registerEvents(new EventListener(), this);
    }
}
