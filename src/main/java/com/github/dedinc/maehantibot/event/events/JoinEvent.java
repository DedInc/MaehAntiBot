package com.github.dedinc.maehantibot.event.events;

import com.github.dedinc.maehantibot.Messages;
import com.github.dedinc.maehantibot.utils.ConfigUtils;
import com.github.dedinc.maehantibot.utils.IPHubUtils;
import com.maxmind.geoip.LookupService;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.nio.file.Paths;

public class JoinEvent implements Listener {

    private JavaPlugin plugin;

    public JoinEvent(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(final AsyncPlayerPreLoginEvent e) {
        final FileConfiguration fc = ConfigUtils.Configs.CONFIG.getConfig();
        if (fc.getBoolean("firewall.enabled")) {
            try {
                LookupService cl = new LookupService(Paths.get("plugins", "MaehAntiBot", "GeoIP.dat").toFile(),
                        LookupService.GEOIP_MEMORY_CACHE | LookupService.GEOIP_CHECK_CACHE);

                String code = cl.getCountry(e.getAddress().getHostName()).getCode();
                boolean isBlocked = fc.getBoolean("firewall.blocked");
                if (isBlocked && fc.getStringList("firewall.list").contains(code)) {
                    e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Messages.firewall);
                    return;
                } else if (!isBlocked && !fc.getStringList("firewall.list").contains(code)) {
                    e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Messages.firewall);
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (fc.getBoolean("iphub.enabled") && IPHubUtils.checkIP(e.getAddress().getHostAddress())) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Messages.proxy);
        }
    }
}
