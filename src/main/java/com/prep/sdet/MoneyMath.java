package com.prep.sdet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Snippet 9: numeric / money.
 * compound: principal * (1 + rate/100/n)^(n*years), rounded HALF_UP to 2 decimals.
 *   principal, rate, years must be >= 0; periodsPerYear must be >= 1 (IAE otherwise).
 * average: arithmetic mean; empty or null list throws IAE.
 * convert: amount * rate rounded HALF_UP to 2 decimals; negative amount or non-positive rate throws IAE.
 */
public final class MoneyMath {
    private MoneyMath() {}

    public static double compound(double principal, double ratePercent, int years, int periodsPerYear) {
        if (principal < 0 || ratePercent < 0 || years < 0 || periodsPerYear < 1) {
            throw new IllegalArgumentException("bad input");
        }
        double factor = Math.pow(1 + ratePercent / 100 / periodsPerYear, (double) periodsPerYear * years);
        return round2(principal * factor);
    }

    public static double average(List<Double> values) {
        if (values == null || values.isEmpty()) throw new IllegalArgumentException("no values");
        double sum = 0;
        for (double v : values) sum += v;
        return sum / values.size();
    }

    public static double convert(double amount, double rate) {
        if (amount < 0 || rate <= 0) throw new IllegalArgumentException("bad input");
        return round2(amount * rate);
    }

    private static double round2(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
