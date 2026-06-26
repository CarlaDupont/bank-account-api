package fr.carla.bank.exception;

public class InvalidAmountException extends RuntimeException {

    public InvalidAmountException() {
        super("Amount must be strictly positive");
    }
}