package com.github.dedinc.maehantibot.tasks;

import com.github.dedinc.maehantibot.MaehAntiBot;
import com.github.dedinc.maehantibot.utils.BlacklistUtils;
import com.github.dedinc.maehantibot.utils.PunishUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.UUID;

public class BlacklistTask extends BukkitRunnable {
    private final String ip;
    private final UUID uuid;
    private final JavaPlugin plugin;

    public BlacklistTask(String ip, UUID uuid) {
        this.plugin = MaehAntiBot.getInstance();
        this.ip = ip;
        this.uuid = uuid;
    }

    @Override
    public void run() {
        if (BlacklistUtils.checkIP(ip)) {
            PunishUtils.punish(ip, uuid, "blacklist");
        }
    }
}
