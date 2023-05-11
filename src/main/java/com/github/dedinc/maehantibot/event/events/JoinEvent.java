package com.github.dedinc.maehantibot.event.events;

import com.github.dedinc.maehantibot.MaehAntiBot;
import com.github.dedinc.maehantibot.Messages;
import com.github.dedinc.maehantibot.Storage;
import com.github.dedinc.maehantibot.utils.ConfigUtils;
import com.github.dedinc.maehantibot.utils.StringUtils;
import com.maxmind.geoip.LookupService;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JoinEvent implements Listener {

    private JavaPlugin plugin;

    public JoinEvent(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(final AsyncPlayerPreLoginEvent e) {
        final String ip = e.getAddress().getHostName();
        final FileConfiguration fc = ConfigUtils.Configs.CONFIG.getConfig();

        if (fc.getBoolean("ping.enabled")) {
            if (handlePing(e, ip)) {
                return;
            }
        }

        if (fc.getBoolean("firewall.enabled")) {
            if (handleFirewall(e, ip, fc)) {
                return;
            }
        }

        if (fc.getBoolean("iphub.enabled")) {
            if (handleIPHub(e, ip, fc)) {
                return;
            }
        }

        if (fc.getBoolean("nicks.enabled")) {
            handleNicks(e, ip, e.getUniqueId(), fc);
        }
    }

    private boolean handlePing(AsyncPlayerPreLoginEvent e, String ip) {
        if (!Storage.hasPinged.contains(ip)) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Messages.ping);
            return true;
        } else {
            Storage.hasPinged.remove(ip);
        }
        return false;
    }

    private boolean handleFirewall(AsyncPlayerPreLoginEvent e, String ip, FileConfiguration fc) {
        try {
            LookupService cl = new LookupService(Paths.get("plugins", "MaehAntiBot", "GeoIP.dat").toFile(),
                    LookupService.GEOIP_MEMORY_CACHE | LookupService.GEOIP_CHECK_CACHE);

            String code = cl.getCountry(ip).getCode();
            boolean isBlocked = fc.getBoolean("firewall.blocked");
            if (isBlocked && fc.getStringList("firewall.list").contains(code)) {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Messages.firewall);
                return true;
            } else if (!isBlocked && !fc.getStringList("firewall.list").contains(code)) {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Messages.firewall);
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private boolean handleIPHub(AsyncPlayerPreLoginEvent e, String ip, FileConfiguration fc) {
        if (MaehAntiBot.iphu.checkIP(e.getAddress().getHostAddress())) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Messages.proxy);
            return true;
        }
        return false;
    }

    private boolean handleNicks(AsyncPlayerPreLoginEvent e, String ip, UUID uuid, FileConfiguration fc) {
        if (Storage.lastNames.size() > 0) {
            String name = getPlayerName(uuid);
            checkNumericNicks(ip, uuid, fc);
            checkRandomizedNicks(ip, uuid, fc);
            boolean flag = false;
            for (UUID uuuid : Storage.lastNames) {
                if (uuuid != uuid) {
                    String nick = getPlayerName(uuuid);
                    if (!name.equals(nick) && StringUtils.checkSimilarity(name, nick) >= fc.getDouble("nicks.coefficient")) {
                        executeActionsForNicks(fc, ip, uuid, uuuid);
                        flag = true;
                    }
                }
            }
            return flag;
        }

        updateLastNamesStorage(uuid, fc);
        return false;
    }

    private void checkNumericNicks(String ip, UUID uuid, FileConfiguration config) {
        Map<String, UUID> numericNicks = getNumericNicks(uuid);

        String playerName = getPlayerName(uuid);
        if (playerName != null && playerName.matches("\\d+")) {
            if (isOverThreshold(numericNicks.size())) {
                for (UUID uuuid : numericNicks.values()) {
                    executeActionsForNicks(config, ip, uuid, uuuid);
                }
            }
        }
    }

    private void checkRandomizedNicks(String ip, UUID uuid, FileConfiguration config) {
        Map<String, UUID> randomizedNicks = getRandomizedNicks(uuid);

        String playerName = getPlayerName(uuid);
        if (playerName != null && isRandomized(playerName)) {
            if (isOverThreshold(randomizedNicks.size())) {
                for (UUID uuuid : randomizedNicks.values()) {
                    executeActionsForNicks(config, ip, uuid, uuuid);
                }
            }
        }
    }

    private Map<String, UUID> getNumericNicks(UUID exclude) {
        return Storage.lastNames.stream()
                .filter(uuid -> !uuid.equals(exclude))
                .collect(Collectors.toMap(
                        uuid -> getPlayerName(uuid),
                        uuid -> uuid,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ))
                .entrySet().stream()
                .filter(entry -> entry.getKey().matches("\\d+"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private HashMap<String, UUID> getRandomizedNicks(UUID exclude) {
        return Storage.lastNames.stream()
                .filter(uuid -> !uuid.equals(exclude))
                .filter(uuid -> isRandomized(getPlayerName(uuid)))
                .collect(Collectors.toMap(
                        uuid -> getPlayerName(uuid),
                        uuid -> uuid,
                        (oldValue, newValue) -> oldValue,
                        HashMap::new
                ));
    }

    private String getPlayerName(UUID uuid) {
        try {
            return Bukkit.getPlayer(uuid).getName();
        } catch (Exception e) {
            return Bukkit.getOfflinePlayer(uuid).getName();
        }
    }

    private boolean isOverThreshold(int nickCount) {
        return (double) nickCount / Storage.lastNames.size() >= 0.5;
    }

    private boolean isRandomized(String nick) {
        final FileConfiguration fc = ConfigUtils.Configs.CONFIG.getConfig();

        int frequentCase = fc.getInt("nicks.frequentCase");

        String alternatingPattern = "(?:[a-zA-Z]\\d){" + frequentCase + ",}";
        String reverseAlternatingPattern = "(?:\\d[a-zA-Z]){" + frequentCase + ",}";
        boolean alternatingLettersNumbers = nick.matches(alternatingPattern) || nick.matches(reverseAlternatingPattern);

        int from = fc.getInt("nicks.prefix.from");
        int to = fc.getInt("nicks.prefix.to");

        Pattern prefixPattern = Pattern.compile("^(.{" + from + "," + to + "})");
        Matcher matcher = prefixPattern.matcher(nick);
        String prefix = matcher.find() ? matcher.group(1) : "";
        boolean hasCommonPrefix = Storage.lastNames.stream().map(uuid -> getPlayerName(uuid)).anyMatch(s -> s.startsWith(prefix));

        boolean frequentCaseChange = nick.matches("(?:(?=.*[a-z])(?=.*[A-Z]).)*{" + frequentCase + ",}");

        return alternatingLettersNumbers || hasCommonPrefix || frequentCaseChange;
    }

    private void executeActionsForNicks(FileConfiguration fc, String ip, UUID uuid, UUID uuuid) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (String action : fc.getStringList("nicks.actions")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), StringUtils.setPlaceholders(action, ip, getPlayerName(uuuid)));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), StringUtils.setPlaceholders(action, ip, getPlayerName(uuid)));
                    }
                }
        }.runTask(plugin);
    }

    private void updateLastNamesStorage(UUID uuid, FileConfiguration fc) {
        if (Storage.lastNames.size() >= fc.getInt("nicks.storage")) {
            Storage.lastNames.clear();
        }
        if (!Storage.lastNames.contains(uuid)) {
            Storage.lastNames.add(uuid);
        }
    }
}