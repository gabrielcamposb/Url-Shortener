package com.url.shortener.util;

import java.security.SecureRandom;

public class ShortenedUrlGenerator {
    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String generate() {
        SecureRandom random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < 7; i++) {
            stringBuilder.append(BASE62.charAt(random.nextInt(BASE62.length())));
        }

        return stringBuilder.toString();
    }
}
