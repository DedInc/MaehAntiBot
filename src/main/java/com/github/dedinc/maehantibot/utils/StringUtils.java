package com.github.dedinc.maehantibot.utils;

import java.util.HashMap;
public class StringUtils {

    public static boolean checkContains(String s, String[] strs, int condition) {
        if (condition < 1) {
            for (String str : strs) {
                if (s.contains(str)) {
                    return true;
                }
            }
        } else {
            int i = 0;
            for (String str : strs) {
                if (s.contains(str)) {
                    i++;
                }
            }
            if (i == strs.length) {
                return true;
            }
        }
        return false;
    }

    public static float checkSimilarity(String s1, String s2) {
        HashMap<Character, Float> a = calculateCharacters(s1);
        HashMap<Character, Float> b = calculateCharacters(s2);
        float i = 0;
        float n = a.keySet().size();
        for (char c : a.keySet()) {
            if (b.keySet().contains(c)) {
                i += a.get(c) < b.get(c) ? a.get(c) / b.get(c) : b.get(c) / a.get(c);
            }
        }
        return i / n;
    }

    private static HashMap<Character, Float> calculateCharacters(String str) {
        HashMap<Character, Float> chars = new HashMap<>();
        int count[] = new int[2048];
        int len = str.length();
        for (int i = 0; i < len; i++) {
            count[str.charAt(i)]++;
        }
        char ch[] = new char[str.length()];
        for (int i = 0; i < len; i++) {
            ch[i] = str.charAt(i);
            int find = 0;
            for (int j = 0; j <= i; j++)
            {
                if (str.charAt(i) == ch[j])
                    find++;
            }
            if (find == 1)
                chars.put(str.charAt(i), (float) count[str.charAt(i)]);
        }
        return chars;
    }

    public static String setPlaceholders(String s, String ip, String name) {
        return s.replaceAll("%ip%",  ip).replaceAll("%player%", name);
    }
}