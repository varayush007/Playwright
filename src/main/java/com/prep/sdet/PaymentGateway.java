package com.prep.sdet;

public interface PaymentGateway {
    /** Throws PaymentException if the charge fails. */
    void charge(String customerId, double amount);
    void refund(String customerId, double amount);
}
