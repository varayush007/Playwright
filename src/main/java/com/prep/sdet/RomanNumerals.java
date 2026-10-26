package com.prep.sdet;

/**
 * Snippet 8b: roman numerals. toRoman accepts 1..3999 (IAE otherwise).
 * toInt accepts uppercase roman characters only (IAE for null, empty or invalid characters).
 * Note: toInt does not reject non-canonical forms such as "IIII". Worth a clarifying question.
 */
public final class RomanNumerals {
    private RomanNumerals() {}

    private static final int[] VALUES = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
    private static final String[] SYMBOLS = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

    public static String toRoman(int n) {
        if (n < 1 || n > 3999) throw new IllegalArgumentException("out of range: " + n);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < VALUES.length; i++) {
            while (n >= VALUES[i]) {
                sb.append(SYMBOLS[i]);
                n -= VALUES[i];
            }
        }
        return sb.toString();
    }

    public static int toInt(String s) {
        if (s == null || s.isEmpty()) throw new IllegalArgumentException("empty");
        int total = 0;
        int prev = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            int v = value(s.charAt(i));
            total += (v < prev) ? -v : v;
            prev = Math.max(prev, v);
        }
        return total;
    }

    private static int value(char c) {
        return switch (c) {
            case 'I' -> 1; case 'V' -> 5; case 'X' -> 10; case 'L' -> 50;
            case 'C' -> 100; case 'D' -> 500; case 'M' -> 1000;
            default -> throw new IllegalArgumentException("invalid char: " + c);
        };
    }
}
