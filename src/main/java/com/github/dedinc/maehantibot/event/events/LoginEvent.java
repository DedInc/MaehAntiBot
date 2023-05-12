package com.github.dedinc.maehantibot.event.events;

import com.github.dedinc.maehantibot.MaehAntiBot;
import com.github.dedinc.maehantibot.Storage;
import com.github.dedinc.maehantibot.tasks.AnalyzeTask;
import com.github.dedinc.maehantibot.tasks.BlacklistTask;
import com.github.dedinc.maehantibot.utils.ConfigUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.UUID;

public class LoginEvent implements Listener {

    private JavaPlugin plugin;

    public LoginEvent() {
        this.plugin = MaehAntiBot.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onLogin(final PlayerLoginEvent e) {
        final FileConfiguration fc = ConfigUtils.Configs.CONFIG.getConfig();
        final String ip = e.getAddress().getHostAddress();
        final UUID uuid = e.getPlayer().getUniqueId();

        if (fc.getBoolean("blacklist.enabled")) {
            new BlacklistTask(ip, uuid).runTask(plugin);
        }

        if (fc.getBoolean("chat.enabled")) {
            Storage.analyzeList.put(uuid, new ArrayList<>());
            Storage.flagList.put(uuid, 0);
            Storage.tasks.put(uuid, new AnalyzeTask(uuid).runTaskLaterAsynchronously(plugin, 20L * fc.getInt("chat.seconds")));
        }
    }
}
