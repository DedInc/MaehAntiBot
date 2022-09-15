package com.github.dedinc.maehantibot.utils;

import okhttp3.*;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.HashMap;

public class RequestUtils {

    public static CookieManager cookieManager;

    public static String post(String url, HashMap<String, String> keys) {
        try {
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            FormBody.Builder builder = new FormBody.Builder();
            for (String key : keys.keySet()) {
                builder.add(key, keys.get(key));
            }
            RequestBody formBody = builder.build();
            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(cookieManager))
                    .build();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();;
            Response response = client.newCall(request).execute();
            String resp = response.body().string();
            response.close();
            return resp;
        } catch (Exception e) {}
        return null;
    }

    public static String get(String url, HashMap<String, String> headers) {
        try {
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(cookieManager))
                    .build();
            okhttp3.Request req;
            Request.Builder reqBuilder = new Request.Builder()
                    .url(url);
            if (headers != null) {
                for (String header : headers.keySet()) {
                    reqBuilder.addHeader(header, headers.get(header));
                }
            }
            req = reqBuilder.build();
            Response res = client.newCall(req).execute();
            String resp = res.body().string();
            res.close();
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
