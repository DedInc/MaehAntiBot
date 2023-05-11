package com.github.dedinc.maehantibot.event.events;

import com.github.dedinc.maehantibot.Storage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PingEvent implements Listener  {

    private JavaPlugin plugin;

    public PingEvent(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPing(final ServerListPingEvent e) {
        if (!Storage.hasPinged.contains(e.getAddress().getHostAddress())) {
            Storage.hasPinged.add(e.getAddress().getHostAddress());
        }
    }
}
