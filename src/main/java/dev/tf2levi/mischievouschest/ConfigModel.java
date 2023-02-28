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
    @ConfigOption(path = "prefix", defaultValue = "§8$ > §e§lMischievousChest: §f")
    private String prefix;
    @ConfigOption(path = "behavior.chance", defaultValue = "50")
    private int trollChance;
    @ConfigOption(path = "behavior.logevent", defaultValue = "true")
    private boolean logMessages;

    public ConfigModel(File configFile) {
        this.configFile = configFile;
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

    public void load(boolean force) throws IOException, InvalidConfigurationException {
        if (!configFile.exists()) {
            save();
            return;
        }

        FileConfiguration config = new YamlConfiguration();
        config.load(configFile);

        Field[] fields = getClass().getDeclaredFields();

        for (Field field : fields) {
            ConfigOption annotation = field.getAnnotation(ConfigOption.class);
            if (annotation != null) {
                String path = annotation.path();
                String defaultValue = annotation.defaultValue();
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
                ConfigOption annotation = field.getAnnotation(ConfigOption.class);
                if (annotation != null && annotation.path().equals(key)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                config.set(key, null);
            }
        }

        for (Field field : fields) {
            ConfigOption annotation = field.getAnnotation(ConfigOption.class);
            if (annotation != null) {
                String path = annotation.path();
                String defaultValue = annotation.defaultValue();
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
            ConfigOption annotation = field.getAnnotation(ConfigOption.class);
            if (annotation != null) {
                String path = annotation.path();
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

        String defaultValue();
    }
}
