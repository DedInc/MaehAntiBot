package com.github.dedinc.maehantibot.utils;

import org.bukkit.configuration.file.FileConfiguration;
import java.util.HashMap;

public class BlacklistUtils {

    public static boolean checkIP(String ip) {
        FileConfiguration fc = ConfigUtils.Configs.CONFIG.getConfig();
        int requiredDetections = fc.getInt("blacklist.detects");
        int detections = getDetectionsCount(ip);

        return detections >= requiredDetections;
    }

    private static int getDetectionsCount(String ip) {
        HashMap<String, String> params = new HashMap<>();
        params.put("ip", ip);
        String response = RequestUtils.post("https://www.ipvoid.com/ip-blacklist-check/", params);
        String[] splitResponse = response.split("Detections Count");

        if (splitResponse.length > 1) {
            String[] countParts = splitResponse[1].split("\">");
            if (countParts.length > 1) {
                String[] countSplit = countParts[1].split("/");
                if (countSplit.length > 0) {
                    try {
                        return Integer.parseInt(countSplit[0]);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return 0;
    }
}