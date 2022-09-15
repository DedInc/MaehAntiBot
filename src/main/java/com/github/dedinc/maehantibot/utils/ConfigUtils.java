package com.github.dedinc.maehantibot.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;

public class ConfigUtils {

    private JavaPlugin plugin;
    private static ConfigUtils instance;

    public ConfigUtils(JavaPlugin plugin) {
        this.plugin = plugin;
        this.instance = this;
        createConfigs();
    }

    private void createConfigs() {
        for(Configs config : Configs.values()) {
            config.init(this);
        }
    }

    public FileConfiguration createConfig(String name) {
        File conf = new File(plugin.getDataFolder(), name);

        if(!conf.exists()) {
            conf.getParentFile().mkdirs();
            plugin.saveResource(name, false);
        }

        FileConfiguration configRet = new YamlConfiguration();

        try {
            configRet.load(conf);
            return configRet;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void saveConfig(FileConfiguration config, String name) {
        try {
            config.save(new File(plugin.getDataFolder(), name));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum Configs {
        CONFIG("config.yml");

        private String name;
        private FileConfiguration config;

        Configs(String name) {
            this.name = name;
        }

        public void init(ConfigUtils holder) {
            this.config = holder.createConfig(name);
        }

        public FileConfiguration getConfig() {
            return config;
        }

        public void saveConfig() {
            instance.saveConfig(config, name);
        }
    }

}