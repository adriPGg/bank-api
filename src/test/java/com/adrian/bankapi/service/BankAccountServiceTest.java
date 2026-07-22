package com.adrian.bankapi.service;

import com.adrian.bankapi.dto.DepositRequest;
import com.adrian.bankapi.dto.WithdrawRequest;
import com.adrian.bankapi.entity.Transaction;
import com.adrian.bankapi.exception.BankAccountNotFoundException;
import com.adrian.bankapi.exception.InsufficientBalanceException;
import com.adrian.bankapi.exception.UnauthorizedAccountAccessException;
import com.adrian.bankapi.exception.UserNotFoundException;
import com.adrian.bankapi.repository.BankAccountRepository;
import com.adrian.bankapi.repository.TransactionRepository;
import com.adrian.bankapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adrian.bankapi.dto.BankAccountRequest;
import com.adrian.bankapi.dto.BankAccountResponse;
import com.adrian.bankapi.entity.AccountType;
import com.adrian.bankapi.entity.BankAccount;
import com.adrian.bankapi.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private BankAccountService bankAccountService;

    @Test
    void bankAccountServiceShouldBeCreated() {

        assertNotNull(bankAccountService);

    }
    @Test
    void createAccount_ShouldCreateAccount_WhenUserExists() {

        // Arrange

        String email = "adrian@test.com";

        User user = new User();
        user.setId(1L);
        user.setEmail(email);

        BankAccountRequest request = new BankAccountRequest();
        request.setAccountType(AccountType.CHECKING);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        BankAccount savedAccount = new BankAccount();
        savedAccount.setId(1L);
        savedAccount.setIban("ES1234567890123456789012");
        savedAccount.setBalance(BigDecimal.ZERO);
        savedAccount.setAccountType(AccountType.CHECKING);
        savedAccount.setCreatedAt(LocalDateTime.now());

        when(bankAccountRepository.save(any(BankAccount.class)))
                .thenReturn(savedAccount);

        // Act

        BankAccountResponse response =
                bankAccountService.createAccount(request, email);

        // Assert

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(BigDecimal.ZERO, response.getBalance());
        assertEquals(AccountType.CHECKING, response.getAccountType());

        verify(userRepository).findByEmail(email);
        verify(bankAccountRepository).save(any(BankAccount.class));
    }

    @Test
    void createAccount_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {

        String email = "adrian@test.com";

        BankAccountRequest request = new BankAccountRequest();
        request.setAccountType(AccountType.CHECKING);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> bankAccountService.createAccount(request, email)
        );

        verify(bankAccountRepository, never())
                .save(any(BankAccount.class));
    }

    @Test
    void deposit_ShouldIncreaseBalance_WhenAccountBelongsToUser() {

        String email = "adrian@test.com";

        User user = new User();
        user.setId(1L);
        user.setEmail(email);

        BankAccount account = new BankAccount();
        account.setId(1L);
        account.setBalance(new BigDecimal("100"));
        account.setIban("ES1234567890123456789012");
        account.setAccountType(AccountType.CHECKING);
        account.setCreatedAt(LocalDateTime.now());

        account.setUser(user);

        DepositRequest request = new DepositRequest();
        request.setAmount(new BigDecimal("50"));

        when(bankAccountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(bankAccountRepository.save(any(BankAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act

        BankAccountResponse response =
                bankAccountService.deposit(1L, request, email);

        // Assert

        assertEquals(new BigDecimal("150"), response.getBalance());

        verify(bankAccountRepository).save(any(BankAccount.class));
        verify(transactionRepository).save(any(Transaction.class));

    }

    @Test
    void deposit_ShouldThrowUnauthorizedAccountAccessException_WhenAccountDoesNotBelongToUser() {

        String email = "adrian@test.com";

        User owner = new User();
        owner.setId(1L);

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail(email);

        BankAccount account = new BankAccount();
        account.setId(1L);
        account.setUser(owner);
        account.setBalance(new BigDecimal("100"));

        DepositRequest request = new DepositRequest();
        request.setAmount(new BigDecimal("50"));

        when(bankAccountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(anotherUser));

        assertThrows(
                UnauthorizedAccountAccessException.class,
                () -> bankAccountService.deposit(1L, request, email)
        );

        verify(bankAccountRepository, never())
                .save(any(BankAccount.class));

        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }

    @Test
    void withdraw_ShouldDecreaseBalance_WhenAccountBelongsToUser() {

        String email = "adrian@test.com";

        User user = new User();
        user.setId(1L);
        user.setEmail(email);

        BankAccount account = new BankAccount();
        account.setId(1L);
        account.setUser(user);
        account.setBalance(new BigDecimal("100"));

        WithdrawRequest request = new WithdrawRequest();
        request.setAmount(new BigDecimal("40"));

        when(bankAccountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(bankAccountRepository.save(any(BankAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BankAccountResponse response =
                bankAccountService.withdraw(1L, request, email);

        assertEquals(new BigDecimal("60"), response.getBalance());

        verify(bankAccountRepository).save(any(BankAccount.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void withdraw_ShouldThrowInsufficientBalanceException_WhenBalanceIsTooLow() {

        String email = "adrian@test.com";

        User user = new User();
        user.setId(1L);
        user.setEmail(email);

        BankAccount account = new BankAccount();
        account.setId(1L);
        account.setUser(user);
        account.setBalance(new BigDecimal("100"));

        WithdrawRequest request = new WithdrawRequest();
        request.setAmount(new BigDecimal("150"));

        when(bankAccountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        assertThrows(
                InsufficientBalanceException.class,
                () -> bankAccountService.withdraw(1L, request, email)
        );

        verify(bankAccountRepository, never())
                .save(any(BankAccount.class));

        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }

    @Test
    void withdraw_ShouldThrowUnauthorizedAccountAccessException_WhenAccountDoesNotBelongToUser() {

        String email = "adrian@test.com";

        User owner = new User();
        owner.setId(1L);

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail(email);

        BankAccount account = new BankAccount();
        account.setId(1L);
        account.setUser(owner);
        account.setBalance(new BigDecimal("100"));

        WithdrawRequest request = new WithdrawRequest();
        request.setAmount(new BigDecimal("40"));

        when(bankAccountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(anotherUser));

        assertThrows(
                UnauthorizedAccountAccessException.class,
                () -> bankAccountService.withdraw(1L, request, email)
        );

        verify(bankAccountRepository, never())
                .save(any(BankAccount.class));

        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }

    @Test
    void getAccountById_ShouldReturnAccount_WhenAccountBelongsToUser() {

        String email = "adrian@test.com";

        User user = new User();
        user.setId(1L);
        user.setEmail(email);

        BankAccount account = new BankAccount();
        account.setId(1L);
        account.setUser(user);
        account.setIban("ES1234567890123456789012");
        account.setBalance(new BigDecimal("250"));
        account.setAccountType(AccountType.CHECKING);
        account.setCreatedAt(LocalDateTime.now());

        when(bankAccountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        BankAccountResponse response =
                bankAccountService.getAccountById(1L, email);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("ES1234567890123456789012", response.getIban());
        assertEquals(new BigDecimal("250"), response.getBalance());
        assertEquals(AccountType.CHECKING, response.getAccountType());

        verify(bankAccountRepository).findById(1L);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void getAccountById_ShouldThrowBankAccountNotFoundException_WhenAccountDoesNotExist() {

        String email = "adrian@test.com";

        when(bankAccountRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                BankAccountNotFoundException.class,
                () -> bankAccountService.getAccountById(1L, email)
        );

        verify(userRepository, never())
                .findByEmail(anyString());
    }
    @Test
    void getAccountById_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {

        String email = "adrian@test.com";

        User owner = new User();
        owner.setId(1L);

        BankAccount account = new BankAccount();
        account.setId(1L);
        account.setUser(owner);

        when(bankAccountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> bankAccountService.getAccountById(1L, email)
        );
    }

    @Test
    void getAccountById_ShouldThrowUnauthorizedAccountAccessException_WhenAccountDoesNotBelongToUser() {

        String email = "adrian@test.com";

        User owner = new User();
        owner.setId(1L);

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail(email);

        BankAccount account = new BankAccount();
        account.setId(1L);
        account.setUser(owner);

        when(bankAccountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(anotherUser));

        assertThrows(
                UnauthorizedAccountAccessException.class,
                () -> bankAccountService.getAccountById(1L, email)
        );
    }

    @Test
    void getMyAccounts_ShouldReturnAllAccounts_WhenUserExists() {

        String email = "adrian@test.com";

        User user = new User();
        user.setId(1L);
        user.setEmail(email);

        BankAccount account1 = new BankAccount();
        account1.setId(1L);
        account1.setIban("ES1111111111111111111111");
        account1.setBalance(new BigDecimal("100"));
        account1.setAccountType(AccountType.CHECKING);
        account1.setCreatedAt(LocalDateTime.now());

        BankAccount account2 = new BankAccount();
        account2.setId(2L);
        account2.setIban("ES2222222222222222222222");
        account2.setBalance(new BigDecimal("500"));
        account2.setAccountType(AccountType.SAVINGS);
        account2.setCreatedAt(LocalDateTime.now());

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(bankAccountRepository.findByUser(user))
                .thenReturn(List.of(account1, account2));

        List<BankAccountResponse> response =
                bankAccountService.getMyAccounts(email);

        assertEquals(2, response.size());

        verify(userRepository).findByEmail(email);
        verify(bankAccountRepository).findByUser(user);
    }

    @Test
    void getMyAccounts_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {

        String email = "adrian@test.com";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> bankAccountService.getMyAccounts(email)
        );

        verify(bankAccountRepository, never())
                .findByUser(any(User.class));
    }
}
