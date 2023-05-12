package com.github.dedinc.maehantibot;

import com.github.dedinc.maehantibot.command.CommandManager;
import com.github.dedinc.maehantibot.event.EventManager;
import com.github.dedinc.maehantibot.utils.ConfigUtils;
import com.github.dedinc.maehantibot.utils.handlers.FirewallHandler;
import com.github.dedinc.maehantibot.utils.handlers.IPHubHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MaehAntiBot extends JavaPlugin {

    private static MaehAntiBot instance;

    @Override
    public void onEnable() {
        instance = this;
        setupConfiguration();
        FirewallHandler.downloadAndExtractGeoIPData();

        FileConfiguration fc = ConfigUtils.Configs.CONFIG.getConfig();

        IPHubHandler.setupIPHub(fc);

        CommandManager.load();
        EventManager.register();
        logFeatureStatus();
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        EventManager.unregister();
        Messages.unloadMessages();
        Storage.cleanup();
    }

    private void setupConfiguration() {
        new ConfigUtils();
        Messages.loadMessages();
    }

    public static void logFeatureStatus() {
        FileConfiguration fc = ConfigUtils.Configs.CONFIG.getConfig();
        if (fc.getBoolean("nicks.enabled")) {
            Bukkit.getLogger().info("Nicks analyze enabled!");
        }
        if (fc.getBoolean("blacklist.enabled")) {
            Bukkit.getLogger().info("Blacklist analyze enabled!");
        }
        if (fc.getBoolean("chat.enabled")) {
            Bukkit.getLogger().info("Chat analyze enabled!");
        }
        if (fc.getBoolean("passwords.enabled")) {
            Bukkit.getLogger().info("Passwords analyze enabled!");
        }
        if (fc.getBoolean("ping.enabled")) {
            Bukkit.getLogger().info("Ping analyze enabled!");
        }
        if (fc.getBoolean("iphub.enabled")) {
            Bukkit.getLogger().info("AntiVPN analyze enabled!");
        }
    }

    public static MaehAntiBot getInstance() {
        return instance;
    }
}