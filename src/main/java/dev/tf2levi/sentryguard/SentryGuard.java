package dev.tf2levi.sentryguard;

import dev.tf2levi.sentryguard.commands.BaseCommand;
import dev.tf2levi.sentryguard.sentry.SentryManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

public final class SentryGuard extends JavaPlugin {
    private static SentryGuard instance;
    private final Logger pluginLogger = this.getLogger();
    private final ConfigModel pluginConfig;
    private final SentryManager sentryManager;

    public SentryGuard() {
        instance = this;

        this.pluginConfig = new ConfigModel(new File(this.getDataFolder(), "Config.yml"));
        this.sentryManager = new SentryManager(this);
    }

    public static SentryGuard getInstance() {
        return instance;
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
        Objects.requireNonNull(this.getCommand("sentryguard")).setExecutor(new BaseCommand(this));
    }

    public Logger getPluginLogger() {
        return pluginLogger;
    }

    public ConfigModel getPluginConfig() {
        return pluginConfig;
    }

    public SentryManager getSentryManager() {
        return sentryManager;
    }
}
