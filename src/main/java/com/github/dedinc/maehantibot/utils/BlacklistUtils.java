package com.github.dedinc.maehantibot.utils;

import org.bukkit.configuration.file.FileConfiguration;
import java.util.HashMap;

public class BlacklistUtils {

    public static boolean checkIP(String ip) {
        final FileConfiguration fc = ConfigUtils.Configs.CONFIG.getConfig();
        HashMap<String, String> params = new HashMap<>();
        params.put("ip", ip);
        String r = RequestUtils.post("https://www.ipvoid.com/ip-blacklist-check/", params);
        int detections = Integer.parseInt(r.split("Detections Count")[1].split("\">")[1].split("/")[0]);

        if (detections >= fc.getInt("blacklist.detects")) {
            return true;
        }

        return false;
    }
}
