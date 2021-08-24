package com.example.whereiseveryone.utils;

public class TextUtils {
    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static String cutSpecialSigns(String string) {
        char[] charSequence = string.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : charSequence) {
            if (c == '.' || c == ',' || c == '$'
                    || c == '#' || c == '[' || c == ']' || c == '/') {
                continue;
            }
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }
}
