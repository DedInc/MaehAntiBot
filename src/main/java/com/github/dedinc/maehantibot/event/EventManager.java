package com.github.dedinc.maehantibot.event;

import com.github.dedinc.maehantibot.event.events.*;
import com.github.dedinc.maehantibot.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class EventManager {

    public static void register(JavaPlugin plugin) {
        final FileConfiguration fc = ConfigUtils.Configs.CONFIG.getConfig();

        if (fc.getBoolean("ping.enabled")) {
            new PingEvent(plugin);
        }

        if (fc.getBoolean("iphub.enabled") || fc.getBoolean("firewall.enabled") || fc.getBoolean("nicks.enabled")) {
            new JoinEvent(plugin);
        }

        if (fc.getBoolean("passwords.enabled") || fc.getBoolean("chat.enabled")) {
            new CommandEvent(plugin);
            Bukkit.getLogger().info("Passwords analyze enabled!");
        }

        if (fc.getBoolean("chat.enabled")) {
            new ChatEvent(plugin);
        }

        if (fc.getBoolean("chat.enabled") || fc.getBoolean("blacklist.enabled")) {
            new LoginEvent(plugin);
        }
    }

    public static void unregister(JavaPlugin plugin) {
        HandlerList.unregisterAll(plugin);
    }
}