package org.stupid.utils;

import java.util.Optional;
import java.util.Random;

public class StupidUtils {

    public static final String NO_RESPONSE_RES = "NO_RESPONSE";
    public static final String UNKNOWN_ERROR = "UNKNOWN_ERROR";

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
        StringBuilder sb = new StringBuilder();
        while(sb.length() < num){
            final int randInd = getRandomNumber(10, 80);
            sb.append(Integer.toHexString(randInd));
        }

        final String hxString = sb.substring(0, num);
        return hexStringToByteArray(hxString);
    }
    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static String cleanErrorTitle(final Exception e) {
        return Optional.ofNullable(e.getMessage()).orElse(UNKNOWN_ERROR);
    }
}
