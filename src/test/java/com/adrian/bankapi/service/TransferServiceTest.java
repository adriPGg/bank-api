package com.adrian.bankapi.service;

import com.adrian.bankapi.dto.TransferRequest;
import com.adrian.bankapi.dto.TransferResponse;
import com.adrian.bankapi.entity.BankAccount;
import com.adrian.bankapi.entity.Transaction;
import com.adrian.bankapi.entity.User;
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

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransferService transferService;

    @Test
    void transfer_ShouldTransferMoney_WhenDataIsValid() {

        String email = "adrian@test.com";

        User user = new User();
        user.setId(1L);
        user.setEmail(email);

        BankAccount fromAccount = new BankAccount();
        fromAccount.setId(1L);
        fromAccount.setUser(user);
        fromAccount.setBalance(new BigDecimal("500"));

        BankAccount toAccount = new BankAccount();
        toAccount.setId(2L);
        toAccount.setBalance(new BigDecimal("100"));

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(1L);
        request.setToAccountId(2L);
        request.setAmount(new BigDecimal("150"));

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(bankAccountRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(fromAccount));

        when(bankAccountRepository.findByIdAndActiveTrue(2L))
                .thenReturn(Optional.of(toAccount));

        when(bankAccountRepository.save(any(BankAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransferResponse response =
                transferService.transfer(request, email);

        assertNotNull(response);
        assertEquals(
                "Transferencia realizada correctamente",
                response.getMessage()
        );

        assertEquals(
                new BigDecimal("350"),
                fromAccount.getBalance()
        );

        assertEquals(
                new BigDecimal("250"),
                toAccount.getBalance()
        );

        verify(bankAccountRepository, times(2))
                .save(any(BankAccount.class));

        verify(transactionRepository)
                .save(any(Transaction.class));
    }

    @Test
    void transfer_ShouldThrow_WhenUserDoesNotExist() {

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(1L);
        request.setToAccountId(2L);
        request.setAmount(new BigDecimal("100"));

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> transferService.transfer(request, "test@test.com")
        );

        verify(bankAccountRepository, never()).findByIdAndActiveTrue(anyLong());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void transfer_ShouldThrow_WhenBalanceIsInsufficient() {

        String email = "adrian@test.com";

        User user = new User();
        user.setId(1L);
        user.setEmail(email);

        BankAccount fromAccount = new BankAccount();
        fromAccount.setId(1L);
        fromAccount.setUser(user);
        fromAccount.setBalance(new BigDecimal("50"));

        BankAccount toAccount = new BankAccount();
        toAccount.setId(2L);
        toAccount.setBalance(new BigDecimal("100"));

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(1L);
        request.setToAccountId(2L);
        request.setAmount(new BigDecimal("150"));

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(bankAccountRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(fromAccount));

        when(bankAccountRepository.findByIdAndActiveTrue(2L))
                .thenReturn(Optional.of(toAccount));

        assertThrows(
                InsufficientBalanceException.class,
                () -> transferService.transfer(request, email)
        );

        verify(bankAccountRepository, never()).save(any(BankAccount.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void transfer_ShouldThrow_WhenUserDoesNotOwnSourceAccount() {

        String email = "adrian@test.com";

        User loggedUser = new User();
        loggedUser.setId(1L);
        loggedUser.setEmail(email);

        User otherUser = new User();
        otherUser.setId(2L);

        BankAccount fromAccount = new BankAccount();
        fromAccount.setId(1L);
        fromAccount.setUser(otherUser);
        fromAccount.setBalance(new BigDecimal("500"));

        BankAccount toAccount = new BankAccount();
        toAccount.setId(2L);
        toAccount.setUser(loggedUser);
        toAccount.setBalance(new BigDecimal("100"));

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(1L);
        request.setToAccountId(2L);
        request.setAmount(new BigDecimal("100"));

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(loggedUser));

        when(bankAccountRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(fromAccount));

        when(bankAccountRepository.findByIdAndActiveTrue(2L))
                .thenReturn(Optional.of(toAccount));

        assertThrows(
                UnauthorizedAccountAccessException.class,
                () -> transferService.transfer(request, email)
        );

        verify(bankAccountRepository, never()).save(any(BankAccount.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void transfer_ShouldThrow_WhenSourceAccountDoesNotExist() {

        String email = "adrian@test.com";

        User user = new User();
        user.setId(1L);
        user.setEmail(email);

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(1L);
        request.setToAccountId(2L);
        request.setAmount(new BigDecimal("100"));

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(bankAccountRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                BankAccountNotFoundException.class,
                () -> transferService.transfer(request, email)
        );

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void transfer_ShouldThrow_WhenDestinationAccountDoesNotExist() {

        String email = "adrian@test.com";

        User user = new User();
        user.setId(1L);
        user.setEmail(email);

        BankAccount fromAccount = new BankAccount();
        fromAccount.setId(1L);
        fromAccount.setUser(user);
        fromAccount.setBalance(new BigDecimal("500"));

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(1L);
        request.setToAccountId(2L);
        request.setAmount(new BigDecimal("100"));

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(bankAccountRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(fromAccount));

        when(bankAccountRepository.findByIdAndActiveTrue(2L))
                .thenReturn(Optional.empty());

        assertThrows(
                BankAccountNotFoundException.class,
                () -> transferService.transfer(request, email)
        );

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

}