package fr.carla.bank.controller;

import fr.carla.bank.exception.AccountAlreadyExistsException;
import fr.carla.bank.exception.AccountNotFoundException;
import fr.carla.bank.exception.InsufficientFundsException;
import fr.carla.bank.exception.InvalidAmountException;
import fr.carla.bank.exception.InvalidTransferException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        String message = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Invalid request");

        return Map.of("error", message);
    }

    @ExceptionHandler({
            InvalidAmountException.class,
            InvalidTransferException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(
            RuntimeException exception
    ) {
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(
            AccountNotFoundException exception
    ) {
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler({
            AccountAlreadyExistsException.class,
            InsufficientFundsException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflict(
            RuntimeException exception
    ) {
        return Map.of("error", exception.getMessage());
    }
}