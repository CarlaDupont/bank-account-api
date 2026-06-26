package fr.carla.bank.exception;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String number) {
        super("Insufficient funds for account: " + number);
    }
}