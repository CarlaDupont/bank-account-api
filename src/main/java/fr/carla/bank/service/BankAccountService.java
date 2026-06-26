package fr.carla.bank.service;

import fr.carla.bank.exception.AccountAlreadyExistsException;
import fr.carla.bank.exception.AccountNotFoundException;
import fr.carla.bank.exception.InsufficientFundsException;
import fr.carla.bank.exception.InvalidAmountException;
import fr.carla.bank.exception.InvalidTransferException;
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
        if (repository.existsByNumber(number)) {
            throw new AccountAlreadyExistsException(number);
        }

        BankAccount account = new BankAccount(
                number,
                holder,
                BigDecimal.ZERO
        );

        return repository.save(account);
    }

    public BankAccount getAccount(String number) {
        return repository
                .findByNumber(number)
                .orElseThrow(() -> new AccountNotFoundException(number));
    }

    public List<BankAccount> getAllAccounts() {
        return repository.findAll();
    }

    public BankAccount deposit(String number, BigDecimal amount) {
        validateAmount(amount);

        BankAccount account = getAccount(number);
        account.deposit(amount);

        return repository.save(account);
    }

    public BankAccount withdraw(String number, BigDecimal amount) {
        validateAmount(amount);

        BankAccount account = getAccount(number);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(number);
        }

        account.withdraw(amount);

        return repository.save(account);
    }

    public void transfer(
            String fromNumber,
            String toNumber,
            BigDecimal amount
    ) {
        validateAmount(amount);

        if (fromNumber.equals(toNumber)) {
            throw new InvalidTransferException(
                    "Source and target accounts must be different"
            );
        }

        BankAccount source = getAccount(fromNumber);
        BankAccount target = getAccount(toNumber);

        if (source.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(fromNumber);
        }

        source.withdraw(amount);
        target.deposit(amount);

        repository.save(source);
        repository.save(target);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException();
        }
    }
}