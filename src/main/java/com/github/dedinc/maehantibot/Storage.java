package com.github.dedinc.maehantibot;

import org.bukkit.scheduler.BukkitTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Storage {

    public static HashMap<UUID, ArrayList<String>> analyzeList = new HashMap<>();
    public static HashMap<UUID, Integer> flagList = new HashMap<>();
    public static HashMap<UUID, BukkitTask> tasks = new HashMap<>();
    public static ArrayList<UUID> lastNames = new ArrayList<>();
    public static HashMap<UUID, String> lastPasswords = new HashMap<>();
    public static ArrayList<String> hasPinged = new ArrayList<>();
    public static ArrayList<String> prefixes = new ArrayList<>();

    public static void cleanup() {
        analyzeList.clear();
        flagList.clear();
        tasks.clear();
        lastNames.clear();
        lastPasswords.clear();
        hasPinged.clear();
        prefixes.clear();
    }
}
