package com.github.dedinc.maehantibot.tasks;

import com.github.dedinc.maehantibot.Storage;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.UUID;

public class AnalyzeTask extends BukkitRunnable {
    private final UUID uuid;

    public AnalyzeTask(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void run() {
        Storage.analyzeList.remove(uuid);
        Storage.flagList.remove(uuid);
        Storage.tasks.remove(uuid);
    }
}
