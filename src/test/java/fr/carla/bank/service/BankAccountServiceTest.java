package fr.carla.bank.service;

import fr.carla.bank.exception.AccountAlreadyExistsException;
import fr.carla.bank.exception.AccountNotFoundException;
import fr.carla.bank.exception.InsufficientFundsException;
import fr.carla.bank.exception.InvalidAmountException;
import fr.carla.bank.model.BankAccount;
import fr.carla.bank.repository.BankAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    @Mock
    private BankAccountRepository repository;

    @InjectMocks
    private BankAccountService service;

    @Test
    void shouldCreateAccountWithZeroBalance() {
        // Arrange
        when(repository.existsByNumber("ACC001")).thenReturn(false);
        when(repository.save(any(BankAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        BankAccount result = service.createAccount("ACC001", "Carla");

        // Assert
        assertEquals("ACC001", result.getNumber());
        assertEquals("Carla", result.getHolder());
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getBalance()));
        verify(repository).save(any(BankAccount.class));
    }

    @Test
    void shouldRejectDuplicateAccountNumber() {
        when(repository.existsByNumber("ACC001")).thenReturn(true);

        assertThrows(
                AccountAlreadyExistsException.class,
                () -> service.createAccount("ACC001", "Carla")
        );

        verify(repository, never()).save(any());
    }

    @Test
    void shouldReturnExistingAccount() {
        BankAccount account =
                new BankAccount("ACC001", "Carla", BigDecimal.ZERO);

        when(repository.findByNumber("ACC001"))
                .thenReturn(Optional.of(account));

        BankAccount result = service.getAccount("ACC001");

        assertEquals("ACC001", result.getNumber());
    }

    @Test
    void shouldRejectMissingAccount() {
        when(repository.findByNumber("UNKNOWN"))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> service.getAccount("UNKNOWN")
        );
    }

    @Test
    void shouldReturnAllAccounts() {
        when(repository.findAll()).thenReturn(List.of(
                new BankAccount("ACC001", "Carla", BigDecimal.ZERO),
                new BankAccount("ACC002", "Alice", BigDecimal.TEN)
        ));

        List<BankAccount> result = service.getAllAccounts();

        assertEquals(2, result.size());
    }

    @Test
    void shouldDepositPositiveAmount() {
        BankAccount account =
                new BankAccount("ACC001", "Carla", BigDecimal.ZERO);

        when(repository.findByNumber("ACC001"))
                .thenReturn(Optional.of(account));

        when(repository.save(account)).thenReturn(account);

        BankAccount result =
                service.deposit("ACC001", new BigDecimal("100.00"));

        assertEquals(
                0,
                new BigDecimal("100.00").compareTo(result.getBalance())
        );
    }

    @Test
    void shouldRejectZeroDeposit() {
        assertThrows(
                InvalidAmountException.class,
                () -> service.deposit("ACC001", BigDecimal.ZERO)
        );
    }

    @Test
    void shouldRejectNegativeDeposit() {
        assertThrows(
                InvalidAmountException.class,
                () -> service.deposit(
                        "ACC001",
                        new BigDecimal("-10.00")
                )
        );
    }

    @Test
    void shouldWithdrawPositiveAmount() {
        BankAccount account = new BankAccount(
                "ACC001",
                "Carla",
                new BigDecimal("100.00")
        );

        when(repository.findByNumber("ACC001"))
                .thenReturn(Optional.of(account));

        when(repository.save(account)).thenReturn(account);

        BankAccount result =
                service.withdraw("ACC001", new BigDecimal("40.00"));

        assertEquals(
                0,
                new BigDecimal("60.00").compareTo(result.getBalance())
        );
    }

    @Test
    void shouldRejectZeroWithdrawal() {
        assertThrows(
                InvalidAmountException.class,
                () -> service.withdraw("ACC001", BigDecimal.ZERO)
        );
    }

    @Test
    void shouldRejectNegativeWithdrawal() {
        assertThrows(
                InvalidAmountException.class,
                () -> service.withdraw(
                        "ACC001",
                        new BigDecimal("-10.00")
                )
        );
    }

    @Test
    void shouldRejectWithdrawalWithInsufficientFunds() {
        BankAccount account = new BankAccount(
                "ACC001",
                "Carla",
                new BigDecimal("20.00")
        );

        when(repository.findByNumber("ACC001"))
                .thenReturn(Optional.of(account));

        assertThrows(
                InsufficientFundsException.class,
                () -> service.withdraw(
                        "ACC001",
                        new BigDecimal("50.00")
                )
        );

        verify(repository, never()).save(any());
    }

    @Test
    void shouldTransferMoneyBetweenAccounts() {
        BankAccount source = new BankAccount(
                "ACC001",
                "Carla",
                new BigDecimal("100.00")
        );

        BankAccount target = new BankAccount(
                "ACC002",
                "Alice",
                BigDecimal.ZERO
        );

        when(repository.findByNumber("ACC001"))
                .thenReturn(Optional.of(source));

        when(repository.findByNumber("ACC002"))
                .thenReturn(Optional.of(target));

        service.transfer(
                "ACC001",
                "ACC002",
                new BigDecimal("40.00")
        );

        assertEquals(
                0,
                new BigDecimal("60.00").compareTo(source.getBalance())
        );

        assertEquals(
                0,
                new BigDecimal("40.00").compareTo(target.getBalance())
        );

        verify(repository).save(source);
        verify(repository).save(target);
    }

    @Test
    void shouldRejectZeroTransfer() {
        assertThrows(
                InvalidAmountException.class,
                () -> service.transfer(
                        "ACC001",
                        "ACC002",
                        BigDecimal.ZERO
                )
        );
    }

    @Test
    void shouldRejectNegativeTransfer() {
        assertThrows(
                InvalidAmountException.class,
                () -> service.transfer(
                        "ACC001",
                        "ACC002",
                        new BigDecimal("-10.00")
                )
        );
    }

    @Test
    void shouldRejectTransferWithInsufficientFunds() {
        BankAccount source = new BankAccount(
                "ACC001",
                "Carla",
                new BigDecimal("10.00")
        );

        BankAccount target = new BankAccount(
                "ACC002",
                "Alice",
                BigDecimal.ZERO
        );

        when(repository.findByNumber("ACC001"))
                .thenReturn(Optional.of(source));

        when(repository.findByNumber("ACC002"))
                .thenReturn(Optional.of(target));

        assertThrows(
                InsufficientFundsException.class,
                () -> service.transfer(
                        "ACC001",
                        "ACC002",
                        new BigDecimal("50.00")
                )
        );
    }

    @Test
    void shouldRejectTransferWhenTargetAccountDoesNotExist() {
        BankAccount source = new BankAccount(
                "ACC001",
                "Carla",
                new BigDecimal("100.00")
        );

        when(repository.findByNumber("ACC001"))
                .thenReturn(Optional.of(source));

        when(repository.findByNumber("UNKNOWN"))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> service.transfer(
                        "ACC001",
                        "UNKNOWN",
                        new BigDecimal("10.00")
                )
        );
    }

    @Test
    void shouldRejectTransferWhenSourceAccountDoesNotExist() {
        when(repository.findByNumber("UNKNOWN"))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> service.transfer(
                        "UNKNOWN",
                        "ACC002",
                        new BigDecimal("10.00")
                )
        );
    }
}