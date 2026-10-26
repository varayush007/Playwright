package com.prep.sdet;

import java.util.ArrayList;
import java.util.List;

/**
 * Snippet 11: pagination. Pages are 1-based.
 * page < 1 or size < 1 throws IAE; null list throws IAE.
 * A page beyond the last returns an empty list. The last page may be partial.
 * Returns a new list (never a view of the input).
 */
public final class Paginator {
    private Paginator() {}

    public static <T> List<T> paginate(List<T> items, int page, int size) {
        if (items == null || page < 1 || size < 1) throw new IllegalArgumentException("bad input");
        int from = (page - 1) * size;
        if (from >= items.size()) return new ArrayList<>();
        int to = Math.min(from + size, items.size() - 1);
        return new ArrayList<>(items.subList(from, to));
    }

    public static int totalPages(int totalItems, int size) {
        if (totalItems < 0 || size < 1) throw new IllegalArgumentException("bad input");
        return (totalItems + size - 1) / size;
    }
}
