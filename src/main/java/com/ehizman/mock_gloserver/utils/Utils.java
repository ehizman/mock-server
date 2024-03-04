package com.ehizman.mock_gloserver.utils;

import java.util.Random;

public class Utils {
    public static String generateAlphanumericString(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Length should be greater than zero");
        }

        // Define the characters that can be used in the alphanumeric string
        String alphanumericCharacters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_";

        // Use StringBuilder to efficiently build the string
        StringBuilder sb = new StringBuilder(n);
        Random random = new Random();

        // Generate the alphanumeric string
        for (int i = 0; i < n; i++) {
            int randomIndex = random.nextInt(alphanumericCharacters.length());
            char randomChar = alphanumericCharacters.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }
}
