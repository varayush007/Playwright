package com.prep.sdet;

/**
 * Snippet 4a: stateful class.
 * deposit/withdraw amounts must be positive (IAE otherwise).
 * Withdrawing more than the balance throws InsufficientFundsException.
 * Withdrawing exactly the full balance is allowed.
 */
public class BankAccount {
    public static class InsufficientFundsException extends RuntimeException {
        public InsufficientFundsException(String m) { super(m); }
    }

    private double balance;

    public BankAccount(double opening) {
        if (opening < 0) throw new IllegalArgumentException("opening balance must not be negative");
        this.balance = opening;
    }

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        balance += amount;
    }

    public void withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        if (amount >= balance) throw new InsufficientFundsException("balance " + balance + ", requested " + amount);
        balance -= amount;
    }

    public double getBalance() { return balance; }
}
