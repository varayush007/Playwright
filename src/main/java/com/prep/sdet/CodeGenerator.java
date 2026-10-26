package com.prep.sdet;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;

/**
 * Snippet 13: randomness (injected Random).
 * next(length): code of 4..32 chars from ABCDEFGHJKLMNPQRSTUVWXYZ23456789, unique per instance.
 * pick(list): random element; empty or null list throws NoSuchElementException.
 */
public class CodeGenerator {
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private final Random random;
    private final Set<String> issued = new HashSet<>();

    public CodeGenerator(Random random) { this.random = random; }

    public String next(int length) {
        if (length < 4 || length > 32) throw new IllegalArgumentException("length must be 4..32");
        while (true) {
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
            String code = sb.toString();
            if (issued.add(code)) return code;
        }
    }

    public <T> T pick(List<T> items) {
        if (items == null || items.isEmpty()) throw new NoSuchElementException("nothing to pick");
        return items.get(random.nextInt(items.size()));
    }
}
