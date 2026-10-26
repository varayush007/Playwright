package com.prep.sdet;

import java.util.*;

/**
 * Snippet 3: collection algorithms.
 * twoSum: indices {i, j} with i < j whose values add to target; empty array if none; null throws IAE.
 * dedupe: removes duplicates, keeps first occurrence order; input is not modified.
 * mergeIntervals: merges overlapping or touching intervals, sorted by start; input is not modified.
 * topK: k largest values, descending; k > length returns all; k < 0 throws IAE.
 */
public final class CollectionAlgos {
    private CollectionAlgos() {}

    public static int[] twoSum(int[] nums, int target) {
        if (nums == null) throw new IllegalArgumentException("null");
        Map<Integer, Integer> seen = new HashMap<>();
        for (int j = 0; j < nums.length; j++) {
            Integer i = seen.get(target - nums[j]);
            if (i != null) return new int[]{i, j};
            seen.put(nums[j], j);
        }
        return new int[0];
    }

    public static <T> List<T> dedupe(List<T> items) {
        if (items == null) throw new IllegalArgumentException("null");
        return new ArrayList<>(new LinkedHashSet<>(items));
    }

    public static List<int[]> mergeIntervals(List<int[]> in) {
        if (in == null) throw new IllegalArgumentException("null");
        List<int[]> sorted = new ArrayList<>();
        for (int[] a : in) sorted.add(new int[]{a[0], a[1]});
        sorted.sort(Comparator.comparingInt(a -> a[0]));
        List<int[]> out = new ArrayList<>();
        for (int[] cur : sorted) {
            if (!out.isEmpty() && cur[0] <= out.get(out.size() - 1)[1]) {
                int[] last = out.get(out.size() - 1);
                last[1] = Math.max(last[1], cur[1]);
            } else {
                out.add(cur);
            }
        }
        return out;
    }

    public static int[] topK(int[] nums, int k) {
        if (nums == null || k < 0) throw new IllegalArgumentException("bad input");
        int[] copy = nums.clone();
        Arrays.sort(copy);
        int n = Math.min(k, copy.length);
        int[] result = new int[n];
        for (int i = 0; i < n; i++) result[i] = copy[copy.length - 1 - i];
        return result;
    }
}
