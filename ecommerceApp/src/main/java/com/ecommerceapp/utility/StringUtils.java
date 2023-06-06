package com.ecommerceapp.utility;

import java.util.stream.Collectors;

public class StringUtils {

    /**
     * Computes the permutation of a string according to the given indices.
     *
     * @param string The string to permute
     * @param indices The indices of the characters in the result, from 1 to {@code string.length()}
     * @return A permutation of the given string
     */
    public static String permute(String string, int[] indices) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int index : indices) {
            stringBuilder.append(string.charAt(index - 1));
        }
        return stringBuilder.toString();
    }

    /**
     * Converts a text string into a binary representation, i.e., a string made of zeros and ones.
     *
     * @param text The text to convert
     * @return The binary representation of the given text
     */
    public static String toBinaryString(String text) {
        return text.chars().mapToObj(i -> String.format("%8s", Integer.toBinaryString(i)).replaceAll(" ", "0")).collect(Collectors.joining());
    }

    public static String zeroPadding(String binaryString, int size) {
        int m = binaryString.length() % size;
        if(m > 0) {
            int zerosToAdd = size - m;
            binaryString = binaryString + "0".repeat(Math.max(0, zerosToAdd));
        }
        return binaryString;
    }

    /**
     * Converts a binary string (a string made of zeros and ones) into a text string.
     *
     * @param binary The binary string to convert
     * @return The converted text string
     */
    public static String binaryToText(String binary) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < binary.length() / 8; i++) {
            int a = Integer.parseInt(binary.substring(8 * i, (i + 1) * 8), 2);
            stringBuilder.append((char) a);
        }
        return stringBuilder.toString();
    }

    /**
     * Splits the given string into an array of strings of the given size.
     *
     * @param string The string to split
     * @param size Size of the strings of the resulting array
     * @return An array of strings of the given size
     */
    public static String[] splitBySize(String string, int size) {
        return string.split(String.format("(?<=\\G.{%d})", size));
    }

    /**
     * Cyclically shifts the string to the left by the given number of characters.
     * The leftmost characters are moved to the right.
     *
     * @param string The string to shift
     * @param shift The number of characters to be shifted
     * @return The shifted string
     */
    public static String cyclicLeftShift(String string, int shift) {
        shift = shift % string.length();
        return string.substring(shift) + string.substring(0, shift);
    }

    /**
     * Computes binary XOR between the two given binary strings.
     *
     * @param str1 First string
     * @param str2 Second strings
     * @return The binary XOR between the two given strings
     * @throws IllegalArgumentException If the two strings have different length
     */
    public static String xor(String str1, String str2) {
        if(str1.length() == str2.length()) {
            StringBuilder stringBuilder = new StringBuilder();
            for(int i = 0; i < str1.length(); i++) {
                stringBuilder.append(str1.charAt(i) ^ str2.charAt(i));
            }
            return stringBuilder.toString();
        } else {
            throw new IllegalArgumentException("The two strings must have the same length");
        }
    }

    /**
     * Converts the given number into a binary string, i.e., a string made of zeros and ones.
     *
     * @param dec The number to convert
     * @return The binary representation of the given number
     */
    public static String toBinaryString(int dec){
        return String.format("%4s", Integer.toBinaryString(dec)).replace(' ', '0');
    }

    /**
     * Converts a binary string (a string made of zeros and ones) into a number.
     *
     * @param str The binary string to convert
     * @return The result of the conversion
     */
    public static int binaryToDecimal(String str){
        return Integer.parseInt(str, 2);
    }
}
