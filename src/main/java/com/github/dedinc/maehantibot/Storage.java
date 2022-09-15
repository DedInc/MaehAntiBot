package com.github.dedinc.maehantibot;

import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;

public class Storage {

    public static HashMap<String, String> analyzeList = new HashMap<>();
    public static HashMap<String, Integer> flagList = new HashMap<>();
    public static HashMap<String, BukkitTask> tasks = new HashMap<>();
    public static ArrayList<String> lastNames = new ArrayList<>();
}
