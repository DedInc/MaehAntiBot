package com.github.dedinc.maehantibot.event.events;

import com.github.dedinc.maehantibot.MaehAntiBot;
import com.github.dedinc.maehantibot.Storage;
import com.github.dedinc.maehantibot.utils.AnalyzeUtils;
import com.github.dedinc.maehantibot.utils.ConfigUtils;
import com.github.dedinc.maehantibot.utils.PunishUtils;
import com.github.dedinc.maehantibot.utils.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;
import java.util.UUID;

public class CommandEvent implements Listener {
    private JavaPlugin plugin;

    public CommandEvent() {
        this.plugin = MaehAntiBot.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onCommand(final PlayerCommandPreprocessEvent e) {
        final String ip = e.getPlayer().getAddress().getHostName();
        final UUID uuid = e.getPlayer().getUniqueId();
        final FileConfiguration fc = ConfigUtils.Configs.CONFIG.getConfig();
        if (fc.getBoolean("passwords.enabled")) {
            final List<String> handles = fc.getStringList("passwords.handleList");
            final String[] matches = handles.toArray(new String[0]);
            if (StringUtils.checkContains(e.getMessage(), matches, 0)) {
                try {
                    String[] dem = e.getMessage().split(" ");
                    String password = dem[dem.length - 1];
                    if (!Storage.lastPasswords.containsKey(uuid)) {
                        Storage.lastPasswords.put(uuid, password);
                    }
                    int i = 0;
                    for (String pass : Storage.lastPasswords.values()) {
                        if (password.equals(pass)) {
                            i++;
                        }
                    }
                    if (i >= fc.getInt("passwords.matches") && Storage.lastPasswords.containsKey(uuid)) {
                        e.setCancelled(true);
                        for (UUID uuuid : Storage.lastPasswords.keySet()) {
                            if (Storage.lastPasswords.get(uuid).equals(password)) {
                                PunishUtils.punish(ip, uuuid, "passwords");
                                return;
                            }
                        }
                        Storage.lastPasswords.clear();
                    }
                    if (Storage.lastPasswords.size() >= fc.getInt("passwords.storage")) {
                        Storage.lastPasswords.clear();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        if (fc.getBoolean("chat.enabled")) {
            final List<String> handles = fc.getStringList("chat.handleList");
            final String[] matches = handles.toArray(new String[0]);
            if (StringUtils.checkContains(e.getMessage(), matches, 0)) {
                AnalyzeUtils.analyzeChat(e.getPlayer(), e.getMessage());
            }
        }
    }
}
