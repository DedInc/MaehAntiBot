package com.github.dedinc.maehantibot;

import com.github.dedinc.maehantibot.utils.ConfigUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;

public class Messages {

    public static String proxy = null;
    public static String firewall = null;

    public static void loadMessages() {
        FileConfiguration fc = ConfigUtils.Configs.CONFIG.getConfig();
        for (Field field : Messages.class.getDeclaredFields()) {
            try {
                field.set(null, fc.getString("messages." + field.getName()).replaceAll("&", "§"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
