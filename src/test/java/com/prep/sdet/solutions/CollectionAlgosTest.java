package com.prep.sdet.solutions;

import static com.prep.sdet.CollectionAlgos.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CollectionAlgosTest {

    private static List<int[]> iv(int[]... a) { return new ArrayList<>(Arrays.asList(a)); }

    private static List<String> str(List<int[]> l) {
        List<String> r = new ArrayList<>();
        for (int[] a : l) r.add(Arrays.toString(a));
        return r;
    }

    // ---------- twoSum ----------
    @DataProvider(name = "twoSumCases")
    public Object[][] twoSumCases() {
        return new Object[][]{
                {new int[]{2, 7, 11, 15}, 9, new int[]{0, 1}},
                {new int[]{3, 2, 4}, 6, new int[]{1, 2}},
                {new int[]{3, 3}, 6, new int[]{0, 1}},            // duplicates
                {new int[]{-3, 4, 3, 90}, 0, new int[]{0, 2}},    // negatives
                {new int[]{0, 4, 0}, 0, new int[]{0, 2}},         // zeros
                {new int[]{3}, 6, new int[0]},                    // element not reused
                {new int[]{}, 5, new int[0]},
                {new int[]{1, 2, 3}, 100, new int[0]},            // no pair
        };
    }

    @Test(dataProvider = "twoSumCases")
    public void twoSum_returnsExpectedIndices(int[] nums, int target, int[] expected) {
        assertEquals(twoSum(nums, target), expected);
    }

    @Test
    public void twoSum_null_throws() {
        assertThrows(IllegalArgumentException.class, () -> twoSum(null, 1));
    }

    @Test
    public void twoSum_doesNotMutateInput() {
        int[] nums = {5, 1, 4};
        twoSum(nums, 9);
        assertEquals(nums, new int[]{5, 1, 4});
    }

    // ---------- dedupe ----------
    @Test
    public void dedupe_keepsFirstOccurrenceOrder() {
        assertEquals(dedupe(List.of(3, 1, 3, 2, 1)), List.of(3, 1, 2));
    }

    @Test
    public void dedupe_emptyAndAllSame() {
        assertEquals(dedupe(List.of()), List.of());
        assertEquals(dedupe(List.of("a", "a", "a")), List.of("a"));
    }

    @Test
    public void dedupe_handlesNullElements() {
        assertEquals(dedupe(Arrays.asList(null, "a", null)), Arrays.asList(null, "a"));
    }

    @Test
    public void dedupe_doesNotMutateInput_andReturnsNewList() {
        List<Integer> input = new ArrayList<>(List.of(1, 1, 2));
        List<Integer> result = dedupe(input);
        assertEquals(input, List.of(1, 1, 2));
        assertNotSame(result, input);
    }

    @Test
    public void dedupe_null_throws() {

        assertThrows(IllegalArgumentException.class, () -> dedupe(null));
    }

    // ---------- mergeIntervals ----------
    @DataProvider(name = "mergeCases")
    public Object[][] mergeCases() {
        return new Object[][]{
                {iv(new int[]{1, 3}, new int[]{2, 6}, new int[]{8, 10}, new int[]{15, 18}),
                        List.of("[1, 6]", "[8, 10]", "[15, 18]")},
                {iv(new int[]{1, 3}, new int[]{3, 5}), List.of("[1, 5]")},                  // touching
                {iv(new int[]{8, 10}, new int[]{1, 3}, new int[]{2, 6}),
                        List.of("[1, 6]", "[8, 10]")},                                        // unsorted
                {iv(new int[]{1, 10}, new int[]{2, 3}, new int[]{4, 5}), List.of("[1, 10]")}, // contained
                {iv(new int[]{1, 2}, new int[]{4, 5}), List.of("[1, 2]", "[4, 5]")},        // disjoint
                {iv(new int[]{1, 5}), List.of("[1, 5]")},                                    // single
                {iv(), List.of()},                                                            // empty
                {iv(new int[]{1, 2}, new int[]{1, 2}), List.of("[1, 2]")},                  // identical
                {iv(new int[]{1, 4}, new int[]{0, 2}, new int[]{3, 5}), List.of("[0, 5]")}, // chain
                {iv(new int[]{-5, -1}, new int[]{-2, 3}), List.of("[-5, 3]")},              // negatives
        };
    }

    @Test(dataProvider = "mergeCases")
    public void mergeIntervals_returnsExpected(List<int[]> input, List<String> expected) {
        assertEquals(str(mergeIntervals(input)), expected);
    }

    @Test
    public void mergeIntervals_doesNotMutateInput() {
        List<int[]> input = iv(new int[]{5, 6}, new int[]{1, 3}, new int[]{2, 4});
        mergeIntervals(input);
        assertEquals(str(input), List.of("[5, 6]", "[1, 3]", "[2, 4]"));
    }

    @Test
    public void mergeIntervals_null_throws() {
        assertThrows(IllegalArgumentException.class, () -> mergeIntervals(null));
    }

    // ---------- topK ----------
    @DataProvider(name = "topKCases")
    public Object[][] topKCases() {
        return new Object[][]{
                {new int[]{5, 1, 9, 3, 7}, 3, new int[]{9, 7, 5}},
                {new int[]{5, 1, 9}, 0, new int[0]},
                {new int[]{5, 1, 9}, 3, new int[]{9, 5, 1}},       // k == length
                {new int[]{5, 1, 9}, 10, new int[]{9, 5, 1}},      // k > length
                {new int[]{4, 4, 1}, 2, new int[]{4, 4}},          // duplicates
                {new int[]{-1, -5, -3}, 2, new int[]{-1, -3}},     // negatives
                {new int[]{}, 3, new int[0]},
        };
    }

    @Test(dataProvider = "topKCases")
    public void topK_returnsLargestDescending(int[] nums, int k, int[] expected) {
        assertEquals(topK(nums, k), expected);
    }

    @Test
    public void topK_doesNotMutateInput() {
        int[] nums = {3, 1, 2};
        topK(nums, 2);
        assertEquals(nums, new int[]{3, 1, 2});
    }

    @Test
    public void topK_invalidInput_throws() {
        assertThrows(IllegalArgumentException.class, () -> topK(new int[]{1}, -1));
        assertThrows(IllegalArgumentException.class, () -> topK(null, 1));
    }
}
