package com.prep.sdet.solutions;

import static com.prep.sdet.MoneyMath.*;
import static org.testng.Assert.*;

import java.util.Arrays;
import java.util.List;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class MoneyMathTest {
    private static final double DELTA = 0.001;

    // ---------- compound ----------
    @DataProvider(name = "compoundCases")
    public Object[][] compoundCases() {
        return new Object[][]{
                // principal, rate%, years, periods, expected
                {1000.0, 5.0, 10, 12, 1647.01},
                {1000.0, 10.0, 1, 1, 1100.0},
                {1000.0, 0.0, 5, 12, 1000.0},      // zero rate
                {1000.0, 5.0, 0, 12, 1000.0},      // zero years
                {0.0, 5.0, 10, 12, 0.0},           // zero principal
                {100.0, 10.0, 2, 1, 121.0},
        };
    }

    @Test(dataProvider = "compoundCases")
    public void compound_returnsExpected(double p, double r, int y, int n, double expected) {
        assertEquals(compound(p, r, y, n), expected, DELTA);
    }

    @Test
    public void compound_largeValues_staysFiniteAndGrows() {
        double result = compound(1e9, 5.0, 30, 12);
        assertTrue(Double.isFinite(result));
        assertTrue(result > 1e9);
    }

    @DataProvider(name = "badCompound")
    public Object[][] badCompound() {
        return new Object[][]{
                {-1.0, 5.0, 1, 1}, {100.0, -0.1, 1, 1}, {100.0, 5.0, -1, 1},
                {100.0, 5.0, 1, 0}, {100.0, 5.0, 1, -12},
        };
    }

    @Test(dataProvider = "badCompound", expectedExceptions = IllegalArgumentException.class)
    public void compound_invalidInput_throws(double p, double r, int y, int n) {
        compound(p, r, y, n);
    }

    // ---------- average ----------
    @Test
    public void average_basic() {
        assertEquals(average(List.of(1.0, 2.0, 3.0)), 2.0, DELTA);
        assertEquals(average(List.of(5.0)), 5.0, DELTA);
        assertEquals(average(List.of(-1.0, 1.0)), 0.0, DELTA);
    }

    @Test
    public void average_floatingPoint_usesDelta() {
        assertEquals(average(Arrays.asList(0.1, 0.2)), 0.15, 1e-9);
    }

    @Test
    public void average_emptyOrNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> average(List.of()));
        assertThrows(IllegalArgumentException.class, () -> average(null));
    }

    // ---------- convert ----------
    @DataProvider(name = "convertCases")
    public Object[][] convertCases() {
        return new Object[][]{
                {100.0, 0.85, 85.0},
                {0.0, 1.5, 0.0},
                {1.0, 0.125, 0.13},     // HALF_UP: 0.125 rounds up (banker's rounding would give 0.12)
                {1.0, 0.124, 0.12},
                {200.0, 83.25, 16650.0},
        };
    }

    @Test(dataProvider = "convertCases")
    public void convert_roundsHalfUpToTwoDecimals(double amount, double rate, double expected) {
        assertEquals(convert(amount, rate), expected, DELTA);
    }

    @DataProvider(name = "badConvert")
    public Object[][] badConvert() {
        return new Object[][]{{-1.0, 1.0}, {10.0, 0.0}, {10.0, -2.0}};
    }

    @Test(dataProvider = "badConvert", expectedExceptions = IllegalArgumentException.class)
    public void convert_invalidInput_throws(double amount, double rate) {
        convert(amount, rate);
    }
}
