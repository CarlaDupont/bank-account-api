package fr.carla.bank.repository;

import fr.carla.bank.model.BankAccount;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository {

    BankAccount save(BankAccount account);

    Optional<BankAccount> findByNumber(String number);

    boolean existsByNumber(String number);

    List<BankAccount> findAll();

    void clear();
}