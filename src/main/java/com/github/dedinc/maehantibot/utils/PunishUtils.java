package com.github.dedinc.maehantibot.utils;

import com.github.dedinc.maehantibot.MaehAntiBot;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.UUID;

public class PunishUtils {

    public static void punish(String ip, UUID uuid, String actions) {
        final FileConfiguration fc = ConfigUtils.Configs.CONFIG.getConfig();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (String action : fc.getStringList(actions + ".actions")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), StringUtils.setPlaceholders(action, ip, InGameUtils.getPlayerName(uuid)));
                }
            }
        }.runTask(MaehAntiBot.getInstance());
    }
}
