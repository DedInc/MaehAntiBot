package com.github.dedinc.maehantibot.utils.handlers;

import com.github.dedinc.maehantibot.Messages;
import com.github.dedinc.maehantibot.Storage;
import com.github.dedinc.maehantibot.utils.ConfigUtils;
import com.github.dedinc.maehantibot.utils.InGameUtils;
import com.github.dedinc.maehantibot.utils.PunishUtils;
import com.github.dedinc.maehantibot.utils.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NicksHandler {

    public NicksHandler() {}

    public boolean handle(AsyncPlayerPreLoginEvent e) {

        final String ip = e.getAddress().getHostName();
        final UUID uuid = e.getUniqueId();

        FileConfiguration fc = ConfigUtils.Configs.CONFIG.getConfig();

        if (Storage.lastNames.size() > 0) {
            String name = InGameUtils.getPlayerName(uuid);
            if (name == null) {
                return false;
            }
            checkPrefixedNicks(e);
            checkNumericNicks(e);
            checkRandomizedNicks(e);
            boolean flag = false;
            for (UUID uuuid : Storage.lastNames) {
                if (uuuid != uuid) {
                    String nick = InGameUtils.getPlayerName(uuuid);
                    if (nick != null && !name.equals(nick) && StringUtils.checkSimilarity(name, nick) >= fc.getDouble("nicks.coefficient")) {
                        PunishUtils.punish(ip, uuuid, "nicks");
                        flag = true;
                    }
                }
            }
            return flag;
        }

        updateLastNamesStorage(uuid);
        return false;
    }

    private void checkNumericNicks(AsyncPlayerPreLoginEvent e) {
        final String ip = e.getAddress().getHostName();
        final UUID uuid = e.getUniqueId();
        Map<String, UUID> numericNicks = getNumericNicks(uuid);

        String playerName = InGameUtils.getPlayerName(uuid);
        if (playerName != null && playerName.matches("\\d+")) {
            if (isOverThreshold(numericNicks.size())) {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Messages.nick);
                for (UUID uuuid : numericNicks.values()) {
                    PunishUtils.punish(ip, uuuid, "nicks");
                }
            }
        }
    }

    private void checkPrefixedNicks(AsyncPlayerPreLoginEvent e) {
        final String ip = e.getAddress().getHostName();
        final UUID uuid = e.getUniqueId();
        String playerName = InGameUtils.getPlayerName(uuid);
        if (playerName != null) {
            if (Storage.prefixes.size() > 0) {
                for (String prefix : Storage.prefixes) {
                    if (playerName.contains(prefix)) {
                        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Messages.nick);
                        PunishUtils.punish(ip, uuid, "nicks");
                    }
                }
            }
        }
    }

    private void checkRandomizedNicks(AsyncPlayerPreLoginEvent e) {
        final String ip = e.getAddress().getHostName();
        final UUID uuid = e.getUniqueId();
        Map<String, UUID> randomizedNicks = getRandomizedNicks(uuid);

        String playerName = InGameUtils.getPlayerName(uuid);
        if (playerName != null && isRandomized(playerName)) {
            if (isOverThreshold(randomizedNicks.size())) {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Messages.nick);
                for (UUID uuuid : randomizedNicks.values()) {
                    PunishUtils.punish(ip, uuuid, "nicks");
                }
            }
        }
    }

    private Map<String, UUID> getNumericNicks(UUID exclude) {
        return Storage.lastNames.stream()
                .filter(uuid -> !uuid.equals(exclude))
                .collect(Collectors.toMap(
                        uuid -> InGameUtils.getPlayerName(uuid),
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
                .filter(uuid -> isRandomized(InGameUtils.getPlayerName(uuid)))
                .collect(Collectors.toMap(
                        uuid -> InGameUtils.getPlayerName(uuid),
                        uuid -> uuid,
                        (oldValue, newValue) -> oldValue,
                        HashMap::new
                ));
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
        boolean hasCommonPrefix = Storage.lastNames.stream().map(uuid -> InGameUtils.getPlayerName(uuid)).anyMatch(s -> s.startsWith(prefix));

        if (hasCommonPrefix && !Storage.prefixes.contains(prefix)) {
            Storage.prefixes.add(prefix);
        }

        boolean frequentCaseChange = nick.matches("(?:(?=.*[a-z])(?=.*[A-Z]).)*{" + frequentCase + ",}");

        return alternatingLettersNumbers || hasCommonPrefix || frequentCaseChange;
    }

    private void updateLastNamesStorage(UUID uuid) {
        FileConfiguration fc = ConfigUtils.Configs.CONFIG.getConfig();
        if ((Storage.lastNames.size() + 1) > fc.getInt("nicks.storage")) {
            Storage.lastNames.clear();
        }

        if (!Storage.lastNames.contains(uuid)) {
            Storage.lastNames.add(uuid);
        }
    }
}
