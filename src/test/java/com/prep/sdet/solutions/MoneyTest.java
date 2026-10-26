package com.prep.sdet.solutions;

import static org.testng.Assert.*;

import com.prep.sdet.Money;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.testng.annotations.Test;

public class MoneyTest {
    private static Money usd(String amount) { return new Money(new BigDecimal(amount), "USD"); }

    @Test
    public void equals_reflexive() {
        Money m = usd("1.00");
        assertEquals(m, m);
    }

    @Test
    public void equals_symmetric() {
        assertEquals(usd("1.0"), usd("1.00"));
        assertEquals(usd("1.00"), usd("1.0"));
    }

    @Test
    public void equals_numericallyEqualAmountsWithDifferentScale() {
        assertEquals(usd("1.0"), usd("1.00"));
    }

    @Test
    public void equals_differentAmountOrCurrency_notEqual() {
        assertNotEquals(usd("1.00"), usd("1.01"));
        assertNotEquals(usd("1.00"), new Money(new BigDecimal("1.00"), "EUR"));
    }

    @Test
    public void equals_nullAndOtherTypes_false() {
        Money m = usd("1.00");
        assertFalse(m.equals(null));
        assertFalse(m.equals("1.00 USD"));
    }

    @Test // FAILS: bug. equals is overridden but hashCode is not
    public void hashCode_equalObjects_haveEqualHashCodes() {
        assertEquals(usd("1.0").hashCode(), usd("1.00").hashCode());
    }

    @Test // FAILS: bug. Same root cause, visible through a HashSet
    public void hashSet_treatsEqualMoneyAsSameElement() {
        Set<Money> set = new HashSet<>();
        set.add(usd("5.0"));
        assertTrue(set.contains(usd("5.00")));
        set.add(usd("5.00"));
        assertEquals(set.size(), 1);
    }

    @Test
    public void constructor_nullArguments_throw() {
        assertThrows(NullPointerException.class, () -> new Money(null, "USD"));
        assertThrows(NullPointerException.class, () -> new Money(BigDecimal.ONE, null));
    }
}
