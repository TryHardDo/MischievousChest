package dev.tf2levi.mischievouschest;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MischievousChest extends JavaPlugin {

    @Override
    public void onEnable() {
        initPlugin();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void initPlugin() {
        Bukkit.getPluginManager().registerEvents(new ListenerClass(this), this);
    }
}
