package dev.tf2levi.mischievouschest;

import dev.tf2levi.mischievouschest.commands.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;

public final class MischievousChest extends JavaPlugin {
    private final Logger pluginLogger = this.getLogger();

    @Override
    public void onEnable() {
        initPlugin();
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }

    private void initPlugin() {
        Bukkit.getPluginManager().registerEvents(new ListenerClass(this), this);
        Utils.LogStartup(Bukkit.getConsoleSender(), this.getDescription());

        // BaseCommand
        Objects.requireNonNull(this.getCommand("mischievouschest")).setExecutor(new BaseCommand(this));
    }

    public Logger getPluginLogger() {
        return pluginLogger;
    }
}
