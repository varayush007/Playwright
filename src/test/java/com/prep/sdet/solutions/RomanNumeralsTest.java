package com.prep.sdet.solutions;

import static com.prep.sdet.RomanNumerals.*;
import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class RomanNumeralsTest {

    @DataProvider(name = "pairs")
    public Object[][] pairs() {
        return new Object[][]{
                {1, "I"}, {3, "III"}, {4, "IV"}, {5, "V"}, {9, "IX"}, {14, "XIV"},
                {40, "XL"}, {58, "LVIII"}, {90, "XC"}, {400, "CD"}, {900, "CM"},
                {1994, "MCMXCIV"}, {3888, "MMMDCCCLXXXVIII"}, {3999, "MMMCMXCIX"},
        };
    }

    @Test(dataProvider = "pairs")
    public void toRoman_convertsCorrectly(int n, String roman) {
        assertEquals(toRoman(n), roman);
    }

    @Test(dataProvider = "pairs")
    public void toInt_convertsCorrectly(int n, String roman) {
        assertEquals(toInt(roman), n);
    }

    @DataProvider(name = "badNumbers")
    public Object[][] badNumbers() { return new Object[][]{{0}, {-1}, {4000}, {Integer.MAX_VALUE}}; }

    @Test(dataProvider = "badNumbers", expectedExceptions = IllegalArgumentException.class)
    public void toRoman_outOfRange_throws(int n) {
        toRoman(n);
    }

    @DataProvider(name = "badRoman")
    public Object[][] badRoman() {
        return new Object[][]{{null}, {""}, {"ABC"}, {"mcm"}, {"X1"}, {"X V"}};
    }

    @Test(dataProvider = "badRoman", expectedExceptions = IllegalArgumentException.class)
    public void toInt_invalidInput_throws(String s) {
        toInt(s);
    }

    @Test
    public void roundTrip_allValidNumbers() {
        for (int n = 1; n <= 3999; n++) {
            assertEquals(toInt(toRoman(n)), n, "round trip failed for " + n);
        }
    }

    @Test // characterization: documents current behaviour, a question for the interviewer
    public void toInt_nonCanonicalForm_isAcceptedAsIs() {
        assertEquals(toInt("IIII"), 4);
    }
}
