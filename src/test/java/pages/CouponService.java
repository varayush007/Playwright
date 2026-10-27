package pages;

import java.time.Clock;
import java.time.LocalDate;

public class CouponService {

    private final Clock clock;

    public CouponService(Clock clock) {
        this.clock = clock;
    }

    public boolean isValid(LocalDate expiryDate) {
        if (expiryDate == null) {
            throw new IllegalArgumentException(
                    "Expiry date cannot be null"
            );
        }

        return !LocalDate.now(clock).isAfter(expiryDate);
    }
}
