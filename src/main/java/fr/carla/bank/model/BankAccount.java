package fr.carla.bank.model;

import java.math.BigDecimal;

public class BankAccount {

    private String number;
    private String holder;
    private BigDecimal balance;

    public BankAccount() {
    }

    public BankAccount(String number, String holder, BigDecimal balance) {
        this.number = number;
        this.holder = holder;
        this.balance = balance;
    }

    public String getNumber() {
        return number;
    }

    public String getHolder() {
        return holder;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        balance = balance.subtract(amount);
    }
}