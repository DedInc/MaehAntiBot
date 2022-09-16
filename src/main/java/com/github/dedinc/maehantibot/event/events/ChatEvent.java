package com.github.dedinc.maehantibot.event.events;

import com.github.dedinc.maehantibot.utils.AnalyzeUtils;
import com.github.dedinc.maehantibot.utils.ConfigUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatEvent implements Listener  {

    private JavaPlugin plugin;

    public ChatEvent(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onChat(final AsyncPlayerChatEvent e) {
        final FileConfiguration fc = ConfigUtils.Configs.CONFIG.getConfig();
        AnalyzeUtils.analyzeChat(plugin, e.getPlayer(), e.getMessage());
    }
}
