package org.stupid.utils;

import java.util.Optional;
import java.util.Random;

public class StupidUtils {
    public static String hex(final byte[] bytes) {
        StringBuilder hexBuilder = new StringBuilder();
        for (byte b : bytes) {
            // Convert each byte to a two-character hex string
            hexBuilder.append(String.format("%02X ", b));
        }
        return hexBuilder.toString();
    }

    public static byte[] hexStringToByteArray(String hexString) {
        // Validate the input string
        if (hexString == null || hexString.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex string must have an even length.");
        }

        int length = hexString.length();
        byte[] byteArray = new byte[length / 2];

        for (int i = 0; i < length; i += 2) {
            // Parse each pair of characters into a byte
            int byteValue = Integer.parseInt(hexString.substring(i, i + 2), 16);
            byteArray[i / 2] = (byte) byteValue;
        }

        return byteArray;
    }

    public static byte[] getRandomHexString(int num){
        final Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < num){
            sb.append(Integer.toHexString(r.nextInt()));
        }

        final String hxString = sb.substring(0, num);
        return hexStringToByteArray(hxString);
    }

    public static String cleanErrorTitle(final Exception e) {
        return Optional.ofNullable(e.getMessage()).orElse("Unknown Error");
    }
}
