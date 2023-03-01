package dev.tf2levi.mischievouschest;

import dev.tf2levi.mischievouschest.commands.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

public final class MischievousChest extends JavaPlugin {
    private final Logger pluginLogger = this.getLogger();
    private final ConfigModel pluginConfig;

    public MischievousChest() {
        this.pluginConfig = new ConfigModel(new File(this.getDataFolder(), "Config.yml"));
    }

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

        try {
            this.pluginConfig.load(false);
        } catch (IOException | InvalidConfigurationException | IllegalAccessException e) {
            pluginLogger.severe("There was an error in the configuration loading algorithm...");
            e.printStackTrace();
        }

        // BaseCommand
        Objects.requireNonNull(this.getCommand("mischievouschest")).setExecutor(new BaseCommand(this));
    }

    public Logger getPluginLogger() {
        return pluginLogger;
    }

    public ConfigModel getPluginConfig() {
        return pluginConfig;
    }
}
