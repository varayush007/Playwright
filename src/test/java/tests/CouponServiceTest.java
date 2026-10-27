package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.CouponService;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class CouponServiceTest {

    private CouponService couponService;

    @BeforeMethod
    public void setUp() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-10-26T10:00:00Z"), ZoneOffset.UTC);
        couponService = new CouponService(fixedClock);
    }

    @Test
    public void couponExpiredYesterday() {
        Assert.assertFalse(
                couponService.isValid(LocalDate.of(2026, 10, 25))
        );
    }

    @Test
    public void couponExpiresToday() {
        Assert.assertTrue(
                couponService.isValid(LocalDate.of(2026, 10, 26))
        );
    }

    @Test
    public void couponExpiresTomorrow() {
        Assert.assertTrue(
                couponService.isValid(LocalDate.of(2026, 10, 27))
        );
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void nullExpiryDateThrowsException() {
        couponService.isValid(null);
    }
}
