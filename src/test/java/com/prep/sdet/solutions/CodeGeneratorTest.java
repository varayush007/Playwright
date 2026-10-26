package com.prep.sdet.solutions;

import static org.testng.Assert.*;

import com.prep.sdet.CodeGenerator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CodeGeneratorTest {

    /** Fake Random that returns the given values from nextInt(bound), in order. */
    private static Random scripted(int... values) {
        return new Random() {
            private int i = 0;
            @Override public int nextInt(int bound) { return values[i++]; }
        };
    }

    @Test
    public void next_hasRequestedLengthAndAllowedAlphabet() {
        String code = new CodeGenerator(new Random(42)).next(8);
        assertTrue(code.matches("[ABCDEFGHJKLMNPQRSTUVWXYZ23456789]{8}"), code);
    }

    @Test
    public void next_sameSeed_sameSequence() {
        CodeGenerator a = new CodeGenerator(new Random(42));
        CodeGenerator b = new CodeGenerator(new Random(42));
        assertEquals(a.next(8), b.next(8));
        assertEquals(a.next(8), b.next(8));
    }

    @Test
    public void next_isUniquePerInstance() {
        CodeGenerator gen = new CodeGenerator(new Random(7));
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 1000; i++) assertTrue(seen.add(gen.next(6)), "duplicate code issued");
    }

    @Test
    public void next_onCollision_retriesUntilUnique() {
        // index 0 = 'A', index 1 = 'B'. 2nd call first collides with "AAAA", then yields "BBBB"
        CodeGenerator gen = new CodeGenerator(scripted(0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1));
        assertEquals(gen.next(4), "AAAA");
        assertEquals(gen.next(4), "BBBB");
    }

    @DataProvider(name = "goodLengths")
    public Object[][] goodLengths() { return new Object[][]{{4}, {5}, {31}, {32}}; }

    @Test(dataProvider = "goodLengths")
    public void next_lengthWithinBounds_ok(int length) {
        assertEquals(new CodeGenerator(new Random(1)).next(length).length(), length);
    }

    @DataProvider(name = "badLengths")
    public Object[][] badLengths() { return new Object[][]{{3}, {0}, {-1}, {33}}; }

    @Test(dataProvider = "badLengths", expectedExceptions = IllegalArgumentException.class)
    public void next_lengthOutOfBounds_throws(int length) {
        new CodeGenerator(new Random(1)).next(length);
    }

    @Test
    public void pick_usesRandomIndex() {
        CodeGenerator gen = new CodeGenerator(scripted(1));
        assertEquals(gen.pick(List.of("a", "b", "c")), "b");
    }

    @Test
    public void pick_singleElement() {
        assertEquals(new CodeGenerator(new Random(1)).pick(List.of("only")), "only");
    }

    @Test
    public void pick_emptyOrNull_throws() {
        CodeGenerator gen = new CodeGenerator(new Random(1));
        assertThrows(NoSuchElementException.class, () -> gen.pick(List.of()));
        assertThrows(NoSuchElementException.class, () -> gen.pick(null));
    }
}
