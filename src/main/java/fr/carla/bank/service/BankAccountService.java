package fr.carla.bank.service;

import fr.carla.bank.model.BankAccount;
import fr.carla.bank.repository.BankAccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BankAccountService {

    private final BankAccountRepository repository;

    public BankAccountService(BankAccountRepository repository) {
        this.repository = repository;
    }

    public BankAccount createAccount(String number, String holder) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public BankAccount getAccount(String number) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<BankAccount> getAllAccounts() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public BankAccount deposit(String number, BigDecimal amount) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public BankAccount withdraw(String number, BigDecimal amount) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void transfer(
            String fromNumber,
            String toNumber,
            BigDecimal amount
    ) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}