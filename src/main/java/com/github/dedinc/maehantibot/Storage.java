package com.github.dedinc.maehantibot;

import org.bukkit.scheduler.BukkitTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Storage {

    public static HashMap<String, String> analyzeList = new HashMap<>();
    public static HashMap<String, Integer> flagList = new HashMap<>();
    public static HashMap<String, BukkitTask> tasks = new HashMap<>();
    public static ArrayList<UUID> lastNames = new ArrayList<>();
    public static ArrayList<String> hasPinged = new ArrayList<>();

    public static void cleanup() {
        analyzeList.clear();
        flagList.clear();
        tasks.clear();
        lastNames.clear();
        hasPinged.clear();
    }
}
