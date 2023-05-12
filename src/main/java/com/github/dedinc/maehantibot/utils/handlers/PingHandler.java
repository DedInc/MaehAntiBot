package com.github.dedinc.maehantibot.utils.handlers;

import com.github.dedinc.maehantibot.Messages;
import com.github.dedinc.maehantibot.Storage;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class PingHandler {

    public PingHandler() {}

    public boolean handle(AsyncPlayerPreLoginEvent e) {
        final String ip = e.getAddress().getHostName();
        if (!Storage.hasPinged.contains(ip)) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Messages.ping);
            return true;
        } else {
            Storage.hasPinged.remove(ip);
        }
        return false;
    }
}
