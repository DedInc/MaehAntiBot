package com.github.dedinc.maehantibot.utils;

import com.github.dedinc.maehantibot.Storage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AnalyzeUtils {

    public static void analyzeChat(JavaPlugin plugin, Player player, String message) {
        String ip = player.getAddress().getHostName();
        String name = player.getName();
        FileConfiguration config = ConfigUtils.Configs.CONFIG.getConfig();

        if (Storage.analyzeList.containsKey(name) && Storage.flagList.containsKey(name)) {
            if (StringUtils.checkSimilarity(Storage.analyzeList.get(name), message) >= config.getDouble("chat.coefficient")) {
                Storage.flagList.put(name, Storage.flagList.get(name) + 1);
            }

            if (Storage.flagList.get(name) >= config.getInt("chat.flags")) {
                cleanUpPlayerData(name);

                Bukkit.getScheduler().runTask(plugin, () -> {
                    for (String action : config.getStringList("chat.actions")) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), StringUtils.setPlaceholders(action, ip, name));
                    }
                });

                return;
            }

            Storage.analyzeList.put(name, message);
        }
    }

    private static void cleanUpPlayerData(String name) {
        Storage.analyzeList.remove(name);
        Storage.flagList.remove(name);

        if (Storage.tasks.containsKey(name)) {
            Storage.tasks.get(name).cancel();
            Storage.tasks.remove(name);
        }
    }
}