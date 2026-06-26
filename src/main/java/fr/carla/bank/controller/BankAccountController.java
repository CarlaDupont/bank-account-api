package fr.carla.bank.controller;

import fr.carla.bank.dto.AmountRequest;
import fr.carla.bank.dto.CreateAccountRequest;
import fr.carla.bank.dto.TransferRequest;
import fr.carla.bank.model.BankAccount;
import fr.carla.bank.service.BankAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {

    private final BankAccountService service;

    public BankAccountController(BankAccountService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BankAccount createAccount(
            @Valid @RequestBody CreateAccountRequest request
    ) {
        return service.createAccount(
                request.number(),
                request.holder()
        );
    }

    @GetMapping
    public List<BankAccount> getAllAccounts() {
        return service.getAllAccounts();
    }

    @GetMapping("/{number}")
    public BankAccount getAccount(
            @PathVariable String number
    ) {
        return service.getAccount(number);
    }

    @PostMapping("/{number}/deposit")
    public BankAccount deposit(
            @PathVariable String number,
            @Valid @RequestBody AmountRequest request
    ) {
        return service.deposit(
                number,
                request.amount()
        );
    }

    @PostMapping("/{number}/withdraw")
    public BankAccount withdraw(
            @PathVariable String number,
            @Valid @RequestBody AmountRequest request
    ) {
        return service.withdraw(
                number,
                request.amount()
        );
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.OK)
    public void transfer(
            @Valid @RequestBody TransferRequest request
    ) {
        service.transfer(
                request.fromNumber(),
                request.toNumber(),
                request.amount()
        );
    }
}