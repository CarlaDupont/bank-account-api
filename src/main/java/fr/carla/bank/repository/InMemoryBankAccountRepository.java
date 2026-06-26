package fr.carla.bank.repository;

import fr.carla.bank.model.BankAccount;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryBankAccountRepository
        implements BankAccountRepository {

    private final ConcurrentHashMap<String, BankAccount> accounts =
            new ConcurrentHashMap<>();

    @Override
    public BankAccount save(BankAccount account) {
        accounts.put(account.getNumber(), account);
        return account;
    }

    @Override
    public Optional<BankAccount> findByNumber(String number) {
        return Optional.ofNullable(accounts.get(number));
    }

    @Override
    public boolean existsByNumber(String number) {
        return accounts.containsKey(number);
    }

    @Override
    public List<BankAccount> findAll() {
        return new ArrayList<>(accounts.values());
    }

    @Override
    public void clear() {
        accounts.clear();
    }
}