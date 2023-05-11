package com.github.dedinc.maehantibot.utils;

import okhttp3.*;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class RequestUtils {

    public static CookieManager cookieManager = new CookieManager();

    private static OkHttpClient createClient(boolean useCookieJar) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

        if (useCookieJar) {
            builder.cookieJar(new JavaNetCookieJar(cookieManager));
        }

        return builder.build();
    }

    public static String post(String url, HashMap<String, String> keys) {
        try {
            FormBody.Builder formBodyBuilder = new FormBody.Builder();

            for (String key : keys.keySet()) {
                formBodyBuilder.add(key, keys.get(key));
            }

            RequestBody formBody = formBodyBuilder.build();

            OkHttpClient client = createClient(!url.contains("ipvoid"));

            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                return response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String get(String url, HashMap<String, String> headers) {
        try {
            OkHttpClient client = createClient(true);

            Request.Builder requestBuilder = new Request.Builder()
                    .url(url);

            if (headers != null) {
                for (String header : headers.keySet()) {
                    requestBuilder.addHeader(header, headers.get(header));
                }
            }

            Request request = requestBuilder.build();

            try (Response response = client.newCall(request).execute()) {
                return response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}