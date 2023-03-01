package dev.tf2levi.mischievouschest;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * TryConfigLoader(TM) Copyright 2023 TryHardDo
 * Efficient, fast, dynamic config management.
 */
public class ConfigModel {

    private final File configFile;
    @ConfigOption(path = "prefix")
    private String prefix = "§8$ > §e§lMischievousChest: §f";
    @ConfigOption(path = "behavior.chance")
    private int trollChance = 50;
    @ConfigOption(path = "behavior.projectile_count")
    private int projectileCount = 20;
    @ConfigOption(path = "behavior.launch_radius")
    private double launchRadius = 10;
    @ConfigOption(path = "behavior.log_event")
    private boolean logMessages = true;

    public ConfigModel(File configFile) {
        this.configFile = configFile;
    }

    public int getProjectileCount() {
        return projectileCount;
    }

    public void setProjectileCount(int projectileCount) {
        this.projectileCount = projectileCount;
    }

    public double getLaunchRadius() {
        return launchRadius;
    }

    public void setLaunchRadius(double launchRadius) {
        this.launchRadius = launchRadius;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getTrollChance() {
        return trollChance;
    }

    public void setTrollChance(int trollChance) {
        this.trollChance = trollChance;
    }

    public boolean getLogMessages() {
        return logMessages;
    }

    public void setLogMessages(boolean logMessages) {
        this.logMessages = logMessages;
    }

    public File getConfigFile() {
        return configFile;
    }

    public void load(boolean force) throws IOException, InvalidConfigurationException, IllegalAccessException {
        if (!configFile.exists()) {
            save();
            return;
        }

        FileConfiguration config = new YamlConfiguration();
        config.load(configFile);

        Field[] fields = getClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(ConfigOption.class)) {
                String path = field.getAnnotation(ConfigOption.class).path();
                Object defaultValue = field.get(this);
                Object value = config.get(path, defaultValue);
                try {
                    field.set(this, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        for (String key : config.getKeys(false)) {
            boolean found = false;
            for (Field field : fields) {
                if (field.isAnnotationPresent(ConfigOption.class)) {
                    String path = field.getAnnotation(ConfigOption.class).path();
                    if (path.equals(key)) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                config.set(key, null);
            }
        }

        for (Field field : fields) {
            if (field.isAnnotationPresent(ConfigOption.class)) {
                String path = field.getAnnotation(ConfigOption.class).path();
                Object defaultValue = field.get(this);
                Object value;
                try {
                    value = field.get(this);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    continue;
                }
                if (!config.contains(path)) {
                    config.set(path, value != null && !force ? value : defaultValue);
                }
            }
        }

        config.save(configFile);
    }


    public void save() throws IOException {
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            configFile.createNewFile();
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        for (Field field : getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigOption.class)) {
                String path = field.getAnnotation(ConfigOption.class).path();
                Object value;
                try {
                    value = field.get(this);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    continue;
                }
                config.set(path, value);
            }
        }

        config.save(configFile);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ConfigOption {
        String path();
    }
}
