package com.ecommerceapp.utility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestStringUtils {

    @Test
    public void testPermute() {
        String permutedString = StringUtils.permute("Hello", new int[] {3, 5, 4, 1, 2});
        Assertions.assertEquals("lolHe", permutedString);
    }

    @Test
    public void testTextToBinary() {
        String binaryString = StringUtils.toBinaryString("Hello");
        Assertions.assertEquals("0100100001100101011011000110110001101111", binaryString);
    }

    @Test
    public void testBinaryToText() {
        String text = StringUtils.binaryToText("0100100001100101011011000110110001101111");
        Assertions.assertEquals("Hello", text);
    }

    @Test
    public void testSplitBySize() {
        String[] result = StringUtils.splitBySize("The quick brown fox jumps over the lazy dog", 4);
        String[] expected = new String[] {"The ", "quic", "k br", "own ", "fox ", "jump", "s ov", "er t", "he l", "azy ", "dog"};
        Assertions.assertArrayEquals(expected, result);
    }

    @Test
    public void testCyclicLeftShift() {
        String result = StringUtils.cyclicLeftShift("12345", 2);
        Assertions.assertEquals("34512", result);
    }

    @Test
    public void testXor() {
        String str1 = "1101010010001110";
        String str2 = "1011000111010100";
        String xor = "0110010101011010";
        Assertions.assertEquals(xor, StringUtils.xor(str1, str2));
    }

    @Test
    public void testXorDifferentLength() {
        String str1 = "1101010";
        String str2 = "1011000111010100010001110";
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.xor(str1, str2));
    }

    @Test
    public void testDecimalToBinary() {
        Assertions.assertEquals("101010", StringUtils.toBinaryString(42));
    }

    @Test
    public void testBinaryToDecimal() {
        Assertions.assertEquals(42, StringUtils.binaryToDecimal("101010"));
    }
}
