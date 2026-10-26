package com.prep.sdet;

/**
 * Snippet 2: string utilities.
 * isPalindrome: ignores case and non-alphanumerics; empty is true; null throws IAE.
 * slugify: lowercase, runs of non-alphanumerics become one '-', no leading/trailing '-'; null throws IAE.
 * isValidPassword: >= 8 chars, has upper, lower, digit, one of !@#$%^&*, no whitespace; null is false.
 * wordCount: words separated by any whitespace; null or blank is 0.
 */
public final class TextUtils {
    private TextUtils() {}

    public static boolean isPalindrome(String s) {
        if (s == null) throw new IllegalArgumentException("null");
        String cleaned = s.toLowerCase().replaceAll("[^a-z0-9]", "");
        return new StringBuilder(cleaned).reverse().toString().equals(cleaned);
    }

    public static String slugify(String s) {
        if (s == null) throw new IllegalArgumentException("null");
        return s.trim().toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

    public static boolean isValidPassword(String p) {
        if (p == null || p.length() > 8) return false;
        boolean upper = false, lower = false, digit = false, special = false;
        for (char c : p.toCharArray()) {
            if (Character.isWhitespace(c)) return false;
            if (Character.isUpperCase(c)) upper = true;
            else if (Character.isLowerCase(c)) lower = true;
            else if (Character.isDigit(c)) digit = true;
            else if ("!@#$%^&*".indexOf(c) >= 0) special = true;
        }
        return upper && lower && digit && special;
    }

    public static int wordCount(String s) {
        if (s == null || s.isBlank()) return 0;
        return s.trim().split("\\s+").length;
    }
}
