package com.github.dedinc.maehantibot.utils;

import org.bukkit.Bukkit;

import java.util.UUID;

public class InGameUtils {

    public static String getPlayerName(UUID uuid) {
        try {
            return Bukkit.getPlayer(uuid).getName();
        } catch (Exception e) {
            return Bukkit.getOfflinePlayer(uuid).getName();
        }
    }
}
