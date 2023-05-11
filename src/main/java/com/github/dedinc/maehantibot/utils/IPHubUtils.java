package com.github.dedinc.maehantibot.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IPHubUtils {

    private String login;
    private String password;
    private String ctoken;
    private String apiKey;

    public IPHubUtils(String login, String password) {
        this.login = login;
        this.password = password;
        this.ctoken = null;
        getSession();
        List<String> keys = getKeys();
        this.apiKey = getKey(keys.get(0));
    }

    private void getSession() {
        try {
            String response = RequestUtils.get("https://iphub.info/login", null);
            ctoken = response.split("token\" content=\"")[1].split("\"")[0];
            HashMap<String, String> params = new HashMap<>();
            params.put("_token", ctoken);
            params.put("email", login);
            params.put("password", password);
            params.put("remember", "on");
            String r = RequestUtils.post("https://iphub.info/login", params);
            if (!r.contains("Logout")) {
                throw new Exception("Login failed!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> getKeys() {
        List<String> keys = new ArrayList<>();
        String response = RequestUtils.get("https://iphub.info/account", null);
        ctoken = response.split("token\" content=\"")[1].split("\"")[0];
        List<String> dem = List.of(response.split("/apiKey/"));
        for (String line : dem) {
            if (dem.indexOf(line) != 0) {
                keys.add(line.split("\"")[0]);
            }
        }
        if (keys.isEmpty()) {
            generateKey();
            return getKeys();
        }
        return keys;
    }

    private String getKey(String id) {
        String response = RequestUtils.get(String.format("https://iphub.info/apiKey/%s", id), null);
        return response.split("readonly value=\"")[1].split("\"")[0];
    }

    private void regenerateKey(String id) {
        HashMap<String, String> params = new HashMap<>();
        params.put("_token", ctoken);
        RequestUtils.post(String.format("https://iphub.info/apiKey/regenerateApiKey/%s", id), params);
        setKey(getKey(id));
    }

    private void generateKey() {
        HashMap<String, String> params = new HashMap<>();
        params.put("_token", ctoken);
        RequestUtils.post("https://iphub.info/apiKey/newFree", params);
    }

    public boolean checkIP(String ip) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("X-Key", apiKey);
        try {
            String response = RequestUtils.get(String.format("http://v2.api.iphub.info/ip/%s", ip), headers);
            JSONObject obj = (JSONObject) new JSONParser().parse(response);
            if (obj.containsKey("error")) {
                if (obj.get("error").toString().contains("86400")) {
                    String firstKey = getKeys().get(0);
                    regenerateKey(firstKey);
                    return checkIP(ip);
                }
            }
            return ((Long) obj.get("block")) != 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setKey(String key) {
        this.apiKey = key;
    }
}