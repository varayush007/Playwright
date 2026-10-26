package com.prep.sdet.solutions;

import static org.testng.Assert.*;

import com.prep.sdet.BankAccount;
import com.prep.sdet.BankAccount.InsufficientFundsException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class BankAccountTest {
    private BankAccount account;

    @BeforeMethod
    public void setUp() { account = new BankAccount(100.0); }

    @Test
    public void newAccount_hasOpeningBalance() {
        assertEquals(account.getBalance(), 100.0, 0.001);
    }

    @Test
    public void zeroOpeningBalance_isAllowed() {
        assertEquals(new BankAccount(0).getBalance(), 0.0, 0.001);
    }

    @Test
    public void negativeOpeningBalance_throws() {
        assertThrows(IllegalArgumentException.class, () -> new BankAccount(-0.01));
    }

    @Test
    public void deposit_increasesBalance() {
        account.deposit(50.5);
        assertEquals(account.getBalance(), 150.5, 0.001);
    }

    @Test
    public void withdraw_partial_decreasesBalance() {
        account.withdraw(40);
        assertEquals(account.getBalance(), 60.0, 0.001);
    }

    @Test // FAILS: bug. Spec says withdrawing the full balance is allowed
    public void withdraw_exactBalance_leavesZero() {
        account.withdraw(100.0);
        assertEquals(account.getBalance(), 0.0, 0.001);
    }

    @Test
    public void withdraw_justUnderBalance_succeeds() {
        account.withdraw(99.99);
        assertEquals(account.getBalance(), 0.01, 0.001);
    }

    @Test
    public void withdraw_justOverBalance_throwsAndBalanceUnchanged() {
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(100.01));
        assertEquals(account.getBalance(), 100.0, 0.001);
    }

    @DataProvider(name = "badAmounts")
    public Object[][] badAmounts() {
        return new Object[][]{{0.0}, {-1.0}, {-0.01}};
    }

    @Test(dataProvider = "badAmounts")
    public void deposit_nonPositive_throwsAndBalanceUnchanged(double amount) {
        assertThrows(IllegalArgumentException.class, () -> account.deposit(amount));
        assertEquals(account.getBalance(), 100.0, 0.001);
    }

    @Test(dataProvider = "badAmounts")
    public void withdraw_nonPositive_throwsAndBalanceUnchanged(double amount) {
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(amount));
        assertEquals(account.getBalance(), 100.0, 0.001);
    }

    @Test
    public void sequenceOfOperations_endsWithCorrectBalance() {
        account.deposit(50);
        account.withdraw(30);
        account.deposit(0.5);
        account.withdraw(20.5);
        assertEquals(account.getBalance(), 100.0, 0.001);
    }
}
