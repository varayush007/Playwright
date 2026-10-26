package com.prep.sdet;

/**
 * Snippet 1: pure function with branches.
 * Rules: premium members and orders of 500 or more ship free.
 * Otherwise fee depends on weight: <=1kg 40, <=5kg 80, <=20kg 150,
 * above 20kg 150 plus 10 per extra kg.
 */
public class ShippingCalculator {
    public double calculate(double weightKg, double orderTotal, boolean premiumMember) {
        if (weightKg <= 0) throw new IllegalArgumentException("weight must be positive");
        if (orderTotal < 0) throw new IllegalArgumentException("total must not be negative");
        if (premiumMember || orderTotal >= 500) return 0.0;
        if (weightKg <= 1) return 40;
        if (weightKg <= 5) return 80;
        if (weightKg <= 20) return 150;
        return 150 + (weightKg - 20) * 10;
    }
}
