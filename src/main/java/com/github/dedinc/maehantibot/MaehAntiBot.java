package com.github.dedinc.maehantibot;

import com.github.dedinc.maehantibot.command.CommandManager;
import com.github.dedinc.maehantibot.event.EventManager;
import com.github.dedinc.maehantibot.utils.ConfigUtils;
import com.github.dedinc.maehantibot.utils.IPHubUtils;
import com.github.dedinc.maehantibot.utils.RequestUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.GZIPInputStream;

public class MaehAntiBot extends JavaPlugin {

    @Override
    public void onEnable() {
        new ConfigUtils(this);
        Messages.loadMessages();

        Path geo = Paths.get("plugins", "MaehAntiBot", "GeoIP.dat.gz");
        Path geoIP = Paths.get("plugins", "MaehAntiBot", "GeoIP.dat");
        if (!geoIP.toFile().exists()) {
            try {
                InputStream in = new URL("https://mailfud.org/geoip-legacy/GeoIP.dat.gz").openStream();
                Files.copy(in, geo, StandardCopyOption.REPLACE_EXISTING);
                try (GZIPInputStream gis = new GZIPInputStream(
                        new FileInputStream(geo.toFile()));
                     FileOutputStream fos = new FileOutputStream(geoIP.toFile())) {

                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = gis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            geo.toFile().delete();
        }

        FileConfiguration fc = ConfigUtils.Configs.CONFIG.getConfig();

        if (fc.getBoolean("iphub.enabled")) {
            RequestUtils.cookieManager = new CookieManager();
            IPHubUtils.getSession(fc.getString("iphub.login"), fc.getString("iphub.password"));
        }

        if (fc.getBoolean("firewall.enabled")) {
            Bukkit.getLogger().info("Firewall enabled!");
        }

        CommandManager.load(this);
        EventManager.register(this);

        if (fc.getBoolean("nicks.enabled")) {
            Bukkit.getLogger().info("Nicks analyze enabled!");
        }
    }

    @Override
    public void onDisable() {}
}
