package com.github.dedinc.maehantibot.utils;

import com.github.dedinc.maehantibot.Storage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AnalyzeUtils {

    public static void analyzeChat(JavaPlugin plugin, Player p, String message) {
        final String name = p.getName();
        final FileConfiguration fc = ConfigUtils.Configs.CONFIG.getConfig();
        if (Storage.analyzeList.containsKey(name) && Storage.flagList.containsKey(name)) {
            if (StringUtils.checkSimilarity(Storage.analyzeList.get(name), message) >= fc.getDouble("chat.coefficient")) {
                Storage.flagList.put(name, Storage.flagList.get(name) + 1);
            }
            if (Storage.flagList.get(name) >= fc.getInt("chat.flags")) {
                Storage.analyzeList.remove(name);
                Storage.flagList.remove(name);
                if (Storage.tasks.containsKey(name)) {
                    Storage.tasks.get(name).cancel();
                    Storage.tasks.remove(name);
                }
                Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), fc.getString("chat.action").replaceAll("%ip%", p.getAddress().getHostName()).replaceAll("%player%", name)));
                return;
            }
            Storage.analyzeList.put(name, message);
        }
    }
}
