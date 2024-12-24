package org.stupid.utils;

public class StupidUtils {
    public static String hex(final byte[] bytes) {
        StringBuilder hexBuilder = new StringBuilder();
        for (byte b : bytes) {
            // Convert each byte to a two-character hex string
            hexBuilder.append(String.format("%02X ", b));
        }
        return hexBuilder.toString();
    }
}
