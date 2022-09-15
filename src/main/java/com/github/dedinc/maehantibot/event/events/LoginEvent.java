package com.github.dedinc.maehantibot.event.events;

import com.github.dedinc.maehantibot.Storage;
import com.github.dedinc.maehantibot.tasks.AnalyzeTask;
import com.github.dedinc.maehantibot.utils.ConfigUtils;
import com.github.dedinc.maehantibot.utils.StringUtils;
import org.bukkit.Bukkit;
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
        final String nick = e.getPlayer().getName();
        if (fc.getBoolean("nicks.enabled")) {
            if (Storage.lastNames.size() > 0) {
                for (String name : Storage.lastNames) {
                    if (!name.equals(nick) && StringUtils.checkSimilarity(name, nick) >= fc.getDouble("nicks.coefficient")) {
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), fc.getString("nicks.action").replaceAll("%player%", name).replaceAll("%ip%", Bukkit.getPlayer(name).getAddress().getHostName()));
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), fc.getString("nicks.action").replaceAll("%player%", nick).replaceAll("%ip%", e.getAddress().getHostName()));
                        });
                        return;
                    }
                }
            }
            if (Storage.lastNames.size() >= fc.getInt("nicks.storage")) {
                Storage.lastNames.clear();
            }
            if (!Storage.lastNames.contains(nick)) {
                Storage.lastNames.add(nick);
            }
        }

        if (fc.getBoolean("chat.enabled")) {
            Storage.analyzeList.put(nick, "");
            Storage.flagList.put(nick, 0);
            Storage.tasks.put(nick, new AnalyzeTask(nick).runTaskLater(plugin, 20 * fc.getInt("chat.seconds")));
        }
    }
}
