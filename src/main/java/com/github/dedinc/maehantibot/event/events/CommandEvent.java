package com.github.dedinc.maehantibot.event.events;

import com.github.dedinc.maehantibot.utils.AnalyzeUtils;
import com.github.dedinc.maehantibot.utils.ConfigUtils;
import com.github.dedinc.maehantibot.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.List;

public class CommandEvent implements Listener {

    private HashMap<String, String> lastPasswords = new HashMap<>();
    private JavaPlugin plugin;

    public CommandEvent(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onCommand(final PlayerCommandPreprocessEvent e) {
        final FileConfiguration fc = ConfigUtils.Configs.CONFIG.getConfig();
        if (fc.getBoolean("passwords.enabled")) {
            final List<String> handles = fc.getStringList("passwords.handleList");
            final String[] matches = handles.toArray(new String[0]);
            if (StringUtils.checkContains(e.getMessage(), matches, 0)) {
                try {
                    String[] dem = e.getMessage().split(" ");
                    String password = dem[dem.length - 1];
                    String player = e.getPlayer().getName();
                    if (!lastPasswords.containsKey(player)) {
                        lastPasswords.put(player, password);
                    }
                    int i = 0;
                    for (String pass : lastPasswords.values()) {
                        if (password.equals(pass)) {
                            i++;
                        }
                    }
                    if (i >= fc.getInt("passwords.matches") && lastPasswords.containsKey(player)) {
                        e.setCancelled(true);
                        for (String nick : lastPasswords.keySet()) {
                            if (lastPasswords.get(player).equals(password)) {
                                Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), fc.getString("passwords.action").replaceAll("%ip%", e.getPlayer().getAddress().getHostName()).replaceAll("%nick%", nick)));
                                return;
                            }
                        }
                        lastPasswords.clear();
                    }
                    if (lastPasswords.size() >= fc.getInt("passwords.storage")) {
                        lastPasswords.clear();
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
                AnalyzeUtils.analyzeChat(plugin, e.getPlayer(), e.getMessage());
            }
        }
    }
}
