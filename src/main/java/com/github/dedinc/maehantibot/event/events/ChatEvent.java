package com.github.dedinc.maehantibot.event.events;

import com.github.dedinc.maehantibot.utils.AnalyzeUtils;
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
        AnalyzeUtils.analyzeChat(plugin, e.getPlayer(), e.getMessage());
    }
}
