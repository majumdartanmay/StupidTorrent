/*
 * MIT License
 *
 * Copyright (c) 2025 Tanmay Majumdar
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package org.stupid.utils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.Random;

public class StupidUtils {

    public static final String NO_RESPONSE_RES = "NO_RESPONSE";
    public static final String UNKNOWN_ERROR = "UNKNOWN_ERROR";
    public static final byte[] STUPID_PEER_ID = "-ST0001-YCM1DCEYEA82".getBytes(StandardCharsets.UTF_8);

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

    public static byte[] getPositiveByteArr(int n) {
        final Random random = new Random();
        final byte[] res = new byte[n];
        for (int i = 0; i < n; ++i) {
            final int seed = random.nextInt() % 127;
            res[i] = (byte)(Math.abs(seed));
        }
        return res;
    }

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static String cleanErrorTitle(final Exception e) {
        return Optional.ofNullable(e.getMessage()).orElse(UNKNOWN_ERROR);
    }

    public static void copyArray(final byte[] src, final byte[] target, final int startSrc, final int endSrc, final int startTarget, final int endTarget) {
        final int srcLength = endSrc - startSrc + 1;
        final int targetLength = endTarget - startTarget + 1;
        if (srcLength <= 0) {
            throw new IllegalStateException("Source array copy space <= 0");
        }

        if (targetLength <= 0) {
            throw new IllegalStateException("Target array copy space <= 0");
        }

        if (srcLength != targetLength) {
            throw new IllegalStateException(String.format("Source copy space of %d is not equal to target copy space of %d", srcLength, targetLength));
        }

        int si = startSrc;
        int ti = startTarget;

        while (si <= endSrc) {
            src[si++] = target[ti++];
        }
    }

    public static byte[] sha1bytes(final byte[] input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        return md.digest(input);
    }

    public static String sha1HexString(byte[] input) {
        try {
            byte[] messageDigest = sha1bytes(input);

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 40 digits long
            while (hashtext.length() < 40) {
                hashtext = String.format("0%s", hashtext);
            }

            // return the HashText
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] convertLongToBytes(final long x) {
        final ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
        byteBuffer.putLong(x);
        return byteBuffer.array();
    }

    public static byte[] convertIntToBytes(final int x) {
        final ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES);
        byteBuffer.putInt(x);
        return byteBuffer.array();
    }

    public static String generateRandomString(int length) {
        // Characters to choose from (uppercase, lowercase, digits)
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder();

        final Random random = new Random();

        // Generate a random string of the specified length
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            randomString.append(characters.charAt(index));
        }

        return randomString.toString();
    }

    public static byte[] convertArrayToUnsigned(final byte[] arr) {
//        final byte[] r = new byte[arr.length];
//
//        for (int i = 0 ; i < arr.length; ++i) {
//            r[i] = Byte.un
//        }
//
//        return r;
        return arr;
    }

    public static byte[] get16BitInteger(final int num) {
        final byte[] data = new byte[2];
        data[0] = (byte) ((num >> 8) & 0xff);
        data[1] = (byte) (num & 0xff);

        return data;
    }

    public static int convertByteArrayToInt(final byte[] arr) {
        final ByteBuffer buffer = ByteBuffer.wrap(arr);
        buffer.order(ByteOrder.BIG_ENDIAN);
        return buffer.getInt();
    }

    public static long convertByteArrayToLong(final byte[] arr) {
        final ByteBuffer buffer = ByteBuffer.wrap(arr);
        buffer.order(ByteOrder.BIG_ENDIAN);
        return buffer.getLong();
    }

}
