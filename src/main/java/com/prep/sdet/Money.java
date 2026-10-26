package com.prep.sdet;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Extra: equals/hashCode contract. Two Money values are equal when currency matches
 * and amounts are numerically equal (1.0 USD equals 1.00 USD).
 */
public final class Money {
    private final BigDecimal amount;
    private final String currency;

    public Money(BigDecimal amount, String currency) {
        this.amount = Objects.requireNonNull(amount);
        this.currency = Objects.requireNonNull(currency);
    }

    public BigDecimal amount() { return amount; }
    public String currency() { return currency; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money m)) return false;
        return amount.compareTo(m.amount) == 0 && currency.equals(m.currency);
    }
}
