package com.github.dedinc.maehantibot.tasks;

import com.github.dedinc.maehantibot.Storage;
import org.bukkit.scheduler.BukkitRunnable;

public class AnalyzeTask extends BukkitRunnable {
    private final String name;

    public AnalyzeTask(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        Storage.analyzeList.remove(name);
        Storage.flagList.remove(name);
        Storage.tasks.remove(name);
    }
}
