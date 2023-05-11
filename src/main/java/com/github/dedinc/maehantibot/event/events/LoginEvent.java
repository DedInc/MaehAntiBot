package com.github.dedinc.maehantibot.event.events;

import com.github.dedinc.maehantibot.Storage;
import com.github.dedinc.maehantibot.tasks.AnalyzeTask;
import com.github.dedinc.maehantibot.tasks.BlacklistTask;
import com.github.dedinc.maehantibot.utils.ConfigUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class LoginEvent implements Listener {

    private JavaPlugin plugin;

    public LoginEvent(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onLogin(final PlayerLoginEvent e) {
        final FileConfiguration fc = ConfigUtils.Configs.CONFIG.getConfig();
        final String ip = e.getAddress().getHostAddress();
        final String nick = e.getPlayer().getName();

        if (fc.getBoolean("blacklist.enabled")) {
            new BlacklistTask(plugin, ip, nick).runTaskAsynchronously(plugin);
        }

        if (fc.getBoolean("chat.enabled")) {
            Storage.analyzeList.put(nick, "");
            Storage.flagList.put(nick, 0);
            Storage.tasks.put(nick, new AnalyzeTask(nick).runTaskLaterAsynchronously(plugin, 20L * fc.getInt("chat.seconds")));
        }
    }
}
