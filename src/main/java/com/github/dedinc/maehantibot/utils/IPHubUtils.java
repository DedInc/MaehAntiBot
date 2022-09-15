package com.github.dedinc.maehantibot.utils;

import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class IPHubUtils {

    private static String csfr = null;
    private static String key = null;

    public static void getSession(String login, String password) {
        csfr = RequestUtils.get("https://iphub.info/login", null).split("token\" content=\"")[1].split("\"")[0];
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("_token", csfr);
        params.put("email", login);
        params.put("password", password);
        params.put("remember", "on");
        final String r = RequestUtils.post("https://iphub.info/login", params);
        if (r.contains("Logout")) {
            if (getKeys().size() == 0) {
                generateKey();
            }
            key = getKey(getKeys().get(0));
            Bukkit.getLogger().info("AntiVPN detection enabled!");
        } else {
            Bukkit.getLogger().warning("IPHub login failed! AntiVPN feature disabled!");
        }
    }

    private static ArrayList<Integer> getKeys() {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        final String r = RequestUtils.get("https://iphub.info/account", null);
        List<String> dem = Arrays.asList(r.split("/apiKey/"));
        for (String line : dem) {
            try {
                if (dem.indexOf(line) != 0) {
                    keys.add(Integer.parseInt(line.split("\"")[0]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return keys;
    }

    private static String getKey(int id) {
        try {
            return "MT" + RequestUtils.get(String.format("https://iphub.info/apiKey/%d", id), null).split("MT")[1].split("\"")[0];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String regenerateKey(int id) {
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("_token", csfr);
            RequestUtils.post(String.format("https://iphub.info/apiKey/regenerateApiKey/%d", id), params);
            return getKey(getKeys().get(0));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String generateKey() {
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("_token", csfr);
            RequestUtils.post("https://iphub.info/apiKey/newFree", params);
            return getKey(getKeys().get(0));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean checkIP(String ip) {
        try {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("X-Key", key);
            JSONObject obj = (JSONObject) new JSONParser().parse(RequestUtils.get(String.format("http://v2.api.iphub.info/ip/%s", ip), headers));
            if (obj.containsKey("error")) {
                if (obj.get("error").toString().contains("86400")) {
                    key = regenerateKey(getKeys().get(0));
                    return checkIP(ip);
                }
            } else {
                return ((Long) obj.get("block")) != 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
