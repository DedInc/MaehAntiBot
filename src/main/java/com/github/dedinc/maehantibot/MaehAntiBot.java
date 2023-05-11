package com.github.dedinc.maehantibot;

import com.github.dedinc.maehantibot.command.CommandManager;
import com.github.dedinc.maehantibot.event.EventManager;
import com.github.dedinc.maehantibot.utils.ConfigUtils;
import com.github.dedinc.maehantibot.utils.IPHubUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.GZIPInputStream;

public class MaehAntiBot extends JavaPlugin {

    public static IPHubUtils iphu = null;

    @Override
    public void onEnable() {
        setupConfiguration();
        downloadAndExtractGeoIPData();

        FileConfiguration fc = ConfigUtils.Configs.CONFIG.getConfig();

        setupIPHub(fc);

        CommandManager.load(this);
        EventManager.register(this);
        logFeatureStatus();
    }

    private void setupConfiguration() {
        new ConfigUtils(this);
        Messages.loadMessages();
    }

    private void downloadAndExtractGeoIPData() {
        Path geo = Paths.get("plugins", "MaehAntiBot", "GeoIP.dat.gz");
        Path geoIP = Paths.get("plugins", "MaehAntiBot", "GeoIP.dat");

        if (geoIP.toFile().exists()) {
            return;
        }

        try {
            InputStream in = new URL("https://mailfud.org/geoip-legacy/GeoIP.dat.gz").openStream();
            Files.copy(in, geo, StandardCopyOption.REPLACE_EXISTING);

            try (GZIPInputStream gis = new GZIPInputStream(new FileInputStream(geo.toFile()));
                 FileOutputStream fos = new FileOutputStream(geoIP.toFile())) {

                byte[] buffer = new byte[1024];
                int len;
                while ((len = gis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        geo.toFile().delete();
    }

    private void setupIPHub(FileConfiguration fc) {
        if (fc.getBoolean("iphub.enabled")) {
            iphu = new IPHubUtils(fc.getString("iphub.login"), fc.getString("iphub.password"));
        }
    }

    public static void logFeatureStatus() {
        FileConfiguration fc = ConfigUtils.Configs.CONFIG.getConfig();
        if (fc.getBoolean("nicks.enabled")) {
            Bukkit.getLogger().info("Nicks analyze enabled!");
        }
        if (fc.getBoolean("blacklist.enabled")) {
            Bukkit.getLogger().info("Blacklist analyze enabled!");
        }
        if (fc.getBoolean("chat.enabled")) {
            Bukkit.getLogger().info("Chat analyze enabled!");
        }
        if (fc.getBoolean("passwords.enabled")) {
            Bukkit.getLogger().info("Passwords analyze enabled!");
        }
        if (fc.getBoolean("ping.enabled")) {
            Bukkit.getLogger().info("Ping analyze enabled!");
        }
        if (fc.getBoolean("iphub.enabled")) {
            Bukkit.getLogger().info("AntiVPN analyze enabled!");
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        EventManager.unregister(this);
        Messages.unloadMessages();
        Storage.cleanup();
    }
}