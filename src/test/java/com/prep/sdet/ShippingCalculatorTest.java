package com.prep.sdet;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ShippingCalculatorTest {
    private ShippingCalculator calculator;

    @BeforeMethod
    public void setUp() {
        calculator = new ShippingCalculator();
    }

    @DataProvider(name = "fees")
    public Object[][] fees() {
        return new Object[][]{
                // weight, orderTotal, premium, expected
                {0.5, 100.0, false, 40.0},     // lightest band
                {1.0, 100.0, false, 40.0},     // upper boundary of band 1
                {1.01, 100.0, false, 80.0},    // just above
                {5.0, 100.0, false, 80.0},
                {5.01, 100.0, false, 150.0},
                {20.0, 100.0, false, 150.0},
                {25.0, 100.0, false, 200.0},   // 150 + 5 * 10
                {10.0, 500.0, false, 0.0},     // free-shipping threshold is inclusive
                {10.0, 499.99, false, 150.0},  // just below threshold
                {10.0, 10.0, true, 0.0},       // premium always free
        };
    }

    @Test(dataProvider = "fees")
    public void calculate_returnsExpectedFee(double weight, double total, boolean premium, double expected) {
        assertEquals(calculator.calculate(weight, total, premium), expected, 0.001);
    }

    @DataProvider(name = "invalid")
    public Object[][] invalid() {
        return new Object[][]{{0.0, 100.0}, {-1.0, 100.0}, {1.0, -0.01}};
    }

    @Test(dataProvider = "invalid", expectedExceptions = IllegalArgumentException.class)
    public void calculate_invalidInput_throws(double weight, double total) {
        calculator.calculate(weight, total, false);
    }
}
