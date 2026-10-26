package com.prep.sdet;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

/** Keep this green: proves your setup works before the interview. */
public class SmokeTest {
    @Test
    public void setupWorks() {
        assertTrue(TextUtils.isPalindrome("A man, a plan, a canal: Panama"));
    }
}
