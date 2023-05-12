package com.github.dedinc.maehantibot.utils.handlers;

import com.github.dedinc.maehantibot.Messages;
import com.github.dedinc.maehantibot.utils.ConfigUtils;
import com.maxmind.geoip.LookupService;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
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

public class FirewallHandler {

    public FirewallHandler() {}


    public boolean handle(AsyncPlayerPreLoginEvent e) {
        try {
            final String ip = e.getAddress().getHostName();
            final FileConfiguration fc = ConfigUtils.Configs.CONFIG.getConfig();
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

    public static void downloadAndExtractGeoIPData() {
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
}
