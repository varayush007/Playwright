package com.prep.sdet.solutions;

import com.prep.sdet.*;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TextUtilsTest {

    // ---------- isPalindrome ----------
    @DataProvider(name = "palindromes")
    public Object[][] palindromes() {
        return new Object[][]{
                {"Ayush hsuyA !!!"},
                {"nitin !!a nitin !!"},
                {"A man, a plan, a canal: Panama"},
                {""},                 // empty is a palindrome per spec
                {"a"},                // single character
                {"12321"},            // digits
                {"!!!"},              // punctuation only: cleaned string is empty. Ask the interviewer
        };
    }

    @Test(dataProvider = "palindromes")
    public void isPalindrome_trueCases(String s) {
        Assert.assertTrue(TextUtils.isPalindrome(s));
    }

    @DataProvider(name = "notPalindromes")
    public Object[][] notPalindromes() {
        return new Object[][]{
                {"qwertyuio"},
                {"bsxhasbxh abshx bashjxb ashjbx @&!*^*&^@"},
                {"bahxb saxbasxbsababsxh asbxh"},
                {"ab"},
        };
    }

    @Test(dataProvider = "notPalindromes")
    public void isPalindrome_falseCases(String s) {
        Assert.assertFalse(TextUtils.isPalindrome(s));
    }

    @Test
    public void isPalindrome_null_throws() {
        Assert.assertThrows(IllegalArgumentException.class, () -> TextUtils.isPalindrome(null));
    }

    @Test // characterization: non-ASCII letters are stripped, so "éa" is judged a palindrome. Questionable
    public void isPalindrome_nonAsciiLettersAreIgnored() {
        Assert.assertTrue(TextUtils.isPalindrome("éa"));
    }

    // ---------- slugify ----------
    @DataProvider(name = "slugCases")
    public Object[][] slugCases() {
        return new Object[][]{
                {"java!!! ayush #&*", "java-ayush"},
                {"nitin !!a nitin !!", "nitin-a-nitin"},
                {"A man, a plan, a canal: Panama", "a-man-a-plan-a-canal-panama"},
                {"", ""},
                {"  Hello   World  ", "hello-world"},   // leading/trailing/multiple spaces
                {"a   --  b", "a-b"},                    // consecutive separators collapse
                {"!!!", ""},                             // only symbols
                {"ABC 123", "abc-123"},                  // case and digits
        };
    }

    @Test(dataProvider = "slugCases")
    public void slugify_returnsExpected(String input, String expected) {
        Assert.assertEquals(TextUtils.slugify(input), expected);
    }

    @Test
    public void slugify_null_throws() {
        Assert.assertThrows(IllegalArgumentException.class, () -> TextUtils.slugify(null));
    }

    // ---------- isValidPassword (spec: >= 8 chars, upper, lower, digit, one of !@#$%^&*, no whitespace) ----------
    @DataProvider(name = "passwordCases")
    public Object[][] passwordCases() {
        return new Object[][]{
                {" ", false},
                {"", false},
                {"abcdfghjk", false},    // no upper, digit, special
                {"AHJSBHSB", false},     // 8 chars but only upper
                {"23456789", false},
                {"%!^%&^@", false},
                {"ababa7272", false},    // no upper, no special
                {"ababaAh", false},
                {"snj^@", false},
                {"A@", false},
                {"A2", false},
                {"252@", false},
                {"12aaAA%$", true},      // exactly 8 chars, lower boundary
                {"23ag^A", false},       // only 6 chars. Too short even though all classes are present
                {"Ab1!", false},         // short with all classes
                {"Aa1!aaa", false},      // 7 chars, just below the boundary
                {"Aa1!aaaa", true},      // 8 chars
                {"Aa1!aaaaa", true},     // 9 chars, just above
                {"Aa1!aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", true}, // long
                {"Aa1! aaaa", false},    // contains whitespace
                {"Aa1~aaaa", false},     // '~' is not an allowed special character
                {"Aaaaaaaa1", false},    // no special
                {"aa1!aaaa", false},     // no upper
                {"AA1!AAAA", false},     // no lower
                {"Aaa!aaaa", false},     // no digit
                {null, false},           // null is false per spec
        };
    }

    @Test(dataProvider = "passwordCases")
    public void isValidPassword_returnsExpected(String password, boolean expected) {
        Assert.assertEquals(TextUtils.isValidPassword(password), expected, "password: " + password);
    }

    // ---------- wordCount ----------
    @DataProvider(name = "wordCountCases")
    public Object[][] wordCountCases() {
        return new Object[][]{
                {"b b b b", 4},
                {"7 snjsn nsjnsj njnjnj mmsm", 5},
                {null, 0},
                {"  ", 0},
                {"", 0},
                {"single", 1},
                {"a\tb\nc", 3},          // tabs and newlines
                {"  a b  ", 2},          // leading/trailing spaces
                {"a    b", 2},           // multiple spaces between words
        };
    }

    @Test(dataProvider = "wordCountCases")
    public void wordCount_returnsExpected(String s, int expected) {
        Assert.assertEquals(TextUtils.wordCount(s), expected);
    }
}
