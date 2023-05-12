package com.github.dedinc.maehantibot.utils;

import com.github.dedinc.maehantibot.Storage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.UUID;

public class AnalyzeUtils {

    public static void analyzeChat(Player player, String message) {
        String ip = player.getAddress().getHostName();
        UUID uuid = player.getUniqueId();
        FileConfiguration config = ConfigUtils.Configs.CONFIG.getConfig();

        if (Storage.analyzeList.containsKey(uuid) && Storage.flagList.containsKey(uuid)) {
            for (String mess: Storage.analyzeList.get(uuid)) {
                if (StringUtils.checkSimilarity(mess, message) >= config.getDouble("chat.coefficient")) {
                    Storage.flagList.put(uuid, Storage.flagList.get(uuid) + 1);
                }
            }

            if (Storage.flagList.get(uuid) >= config.getInt("chat.flags")) {
                cleanUpPlayerData(uuid);
                PunishUtils.punish(ip, uuid, "chat");

                return;
            }

            if ((Storage.analyzeList.get(uuid).size() + 1) > config.getInt("chat.storage")) {
                Storage.analyzeList.get(uuid).clear();
            }
            Storage.analyzeList.get(uuid).add(message);
        }
    }

    private static void cleanUpPlayerData(UUID uuid) {
        Storage.analyzeList.remove(uuid);
        Storage.flagList.remove(uuid);

        if (Storage.tasks.containsKey(uuid)) {
            Storage.tasks.get(uuid).cancel();
            Storage.tasks.remove(uuid);
        }
    }
}