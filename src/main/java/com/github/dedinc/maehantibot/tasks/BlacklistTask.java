package com.github.dedinc.maehantibot.tasks;

import com.github.dedinc.maehantibot.utils.BlacklistUtils;
import com.github.dedinc.maehantibot.utils.ConfigUtils;
import com.github.dedinc.maehantibot.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BlacklistTask extends BukkitRunnable {
    private final String ip;
    private final String name;
    private final JavaPlugin plugin;

    public BlacklistTask(JavaPlugin plugin, String ip, String name) {
        this.plugin = plugin;
        this.ip = ip;
        this.name = name;
    }

    @Override
    public void run() {
        final FileConfiguration fc = ConfigUtils.Configs.CONFIG.getConfig();
        if (BlacklistUtils.checkIP(ip)) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                for (String action : fc.getStringList("blacklist.actions")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), StringUtils.setPlaceholders(action, ip, name));
                }
            });
        }
    }
}
