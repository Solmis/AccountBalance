package net.solmis.accountbalance;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;

public class Account {
    private String name;
    private BigInteger number;
    private BigDecimal balance;
    private Currency currency;

    Account(String name, BigInteger number, BigDecimal balance, Currency currency) {
        this.name = name;
        this.number = number;
        this.balance = balance;
        this.currency = currency;
    }

    @Override
    public String toString() {
        return number.toString() + " (" + name + "): " + balance.toString() + " " + currency.getCurrencyCode();
    }
}
