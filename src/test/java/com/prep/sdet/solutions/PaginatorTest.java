package com.prep.sdet.solutions;

import static com.prep.sdet.Paginator.*;
import static org.testng.Assert.*;

import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class PaginatorTest {
    private static final List<Integer> FIVE = List.of(1, 2, 3, 4, 5);

    @DataProvider(name = "pageCases")
    public Object[][] pageCases() {
        return new Object[][]{
                {FIVE, 1, 2, List.of(1, 2)},
                {FIVE, 2, 2, List.of(3, 4)},
                {FIVE, 3, 2, List.of(5)},                   // FAILS: last partial page loses its element
                {FIVE, 4, 2, List.of()},                    // beyond range
                {FIVE, 1, 10, FIVE},                        // FAILS: size larger than list drops the last element
                {FIVE, 1, 5, FIVE},                         // FAILS: exact fit
                {List.of(1, 2, 3, 4), 2, 2, List.of(3, 4)}, // FAILS: exact multiple, last page
                {List.of(), 1, 5, List.of()},               // empty list
                {List.of(7), 1, 1, List.of(7)},             // FAILS: single element
                {FIVE, 100, 2, List.of()},                  // far beyond range
        };
    }

    @Test(dataProvider = "pageCases")
    public void paginate_returnsExpectedPage(List<Integer> items, int page, int size, List<Integer> expected) {
        assertEquals(paginate(items, page, size), expected);
    }

    @DataProvider(name = "badArgs")
    public Object[][] badArgs() {
        return new Object[][]{{0, 5}, {-1, 5}, {1, 0}, {1, -3}};
    }

    @Test(dataProvider = "badArgs", expectedExceptions = IllegalArgumentException.class)
    public void paginate_invalidPageOrSize_throws(int page, int size) {
        paginate(FIVE, page, size);
    }

    @Test
    public void paginate_nullList_throws() {
        assertThrows(IllegalArgumentException.class, () -> paginate(null, 1, 5));
    }

    @Test
    public void paginate_returnsIndependentList() {
        List<Integer> source = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        List<Integer> page = paginate(source, 1, 2);
        page.add(99);
        assertEquals(source, List.of(1, 2, 3, 4, 5));
    }

    @DataProvider(name = "totalPagesCases")
    public Object[][] totalPagesCases() {
        return new Object[][]{{0, 5, 0}, {1, 5, 1}, {5, 5, 1}, {6, 5, 2}, {10, 3, 4}, {10, 1, 10}};
    }

    @Test(dataProvider = "totalPagesCases")
    public void totalPages_roundsUp(int total, int size, int expected) {
        assertEquals(totalPages(total, size), expected);
    }

    @Test
    public void totalPages_invalidInput_throws() {
        assertThrows(IllegalArgumentException.class, () -> totalPages(-1, 5));
        assertThrows(IllegalArgumentException.class, () -> totalPages(5, 0));
    }
}
