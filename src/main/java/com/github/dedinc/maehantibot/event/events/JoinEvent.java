package com.github.dedinc.maehantibot.event.events;

import com.github.dedinc.maehantibot.MaehAntiBot;
import com.github.dedinc.maehantibot.utils.ConfigUtils;
import com.github.dedinc.maehantibot.utils.handlers.FirewallHandler;
import com.github.dedinc.maehantibot.utils.handlers.IPHubHandler;
import com.github.dedinc.maehantibot.utils.handlers.NicksHandler;
import com.github.dedinc.maehantibot.utils.handlers.PingHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class JoinEvent implements Listener {

    private JavaPlugin plugin;
    private PingHandler pingHandler;
    private FirewallHandler firewallHandler;
    private IPHubHandler ipHubHandler;
    private NicksHandler nicksHandler;

    public JoinEvent() {
        this.plugin = MaehAntiBot.getInstance();
        this.pingHandler = new PingHandler();
        this.firewallHandler = new FirewallHandler();
        this.ipHubHandler = new IPHubHandler();
        this.nicksHandler = new NicksHandler();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(final AsyncPlayerPreLoginEvent e) {
        final FileConfiguration fc = ConfigUtils.Configs.CONFIG.getConfig();

        if (fc.getBoolean("ping.enabled") && pingHandler.handle(e)) {
            return;
        }

        if (fc.getBoolean("firewall.enabled") && firewallHandler.handle(e)) {
            return;
        }

        if (fc.getBoolean("iphub.enabled") && ipHubHandler.handle(e)) {
            return;
        }

        if (fc.getBoolean("nicks.enabled") && nicksHandler.handle(e)) {
            return;
        }
    }
}