package com.github.dedinc.maehantibot.utils.handlers;

import com.github.dedinc.maehantibot.Messages;
import com.github.dedinc.maehantibot.utils.IPHubUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class IPHubHandler {

    public static IPHubUtils iphub = null;

    public IPHubHandler() {}

    public boolean handle(AsyncPlayerPreLoginEvent e) {
        if (iphub.checkIP(e.getAddress().getHostAddress())) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Messages.proxy);
            return true;
        }
        return false;
    }

    public static void setupIPHub(FileConfiguration fc) {
        if (fc.getBoolean("iphub.enabled")) {
            iphub = new IPHubUtils(fc.getString("iphub.login"), fc.getString("iphub.password"));
        }
    }
}
