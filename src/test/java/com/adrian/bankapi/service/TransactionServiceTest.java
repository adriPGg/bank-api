package com.adrian.bankapi.service;

import com.adrian.bankapi.entity.TransactionType;
import com.adrian.bankapi.exception.BankAccountNotFoundException;
import com.adrian.bankapi.exception.UnauthorizedAccountAccessException;
import org.springframework.data.jpa.domain.Specification;
import com.adrian.bankapi.dto.TransactionResponse;
import com.adrian.bankapi.entity.BankAccount;
import com.adrian.bankapi.entity.User;

import com.adrian.bankapi.entity.Transaction;

import org.springframework.data.domain.PageImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import com.adrian.bankapi.exception.UserNotFoundException;
import com.adrian.bankapi.mapper.TransactionMapper;
import com.adrian.bankapi.repository.BankAccountRepository;
import com.adrian.bankapi.repository.TransactionRepository;
import com.adrian.bankapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void getMyTransactions_ShouldThrow_WhenUserDoesNotExist() {

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> transactionService.getMyTransactions(
                        "test@test.com",
                        null,
                        0,
                        10
                )
        );
    }
    @Test
    void getMyTransactions_ShouldReturnEmptyPage_WhenUserHasNoTransactions() {

        User user = new User();
        user.setEmail("test@test.com");

        BankAccount account = new BankAccount();

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(bankAccountRepository.findByUser(user))
                .thenReturn(List.of(account));

        when(transactionRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(Page.empty());

        Page<TransactionResponse> result =
                transactionService.getMyTransactions(
                        "test@test.com",
                        null,
                        0,
                        10
                );

        assertTrue(result.isEmpty());
    }

    @Test
    void getMyTransactions_ShouldReturnTransactions() {

        User user = new User();
        user.setEmail("test@test.com");

        BankAccount account = new BankAccount();

        Transaction transaction = new Transaction();

        TransactionResponse response = new TransactionResponse();

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(bankAccountRepository.findByUser(user))
                .thenReturn(List.of(account));

        when(transactionRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(transaction)));

        when(transactionMapper.toResponse(transaction))
                .thenReturn(response);

        Page<TransactionResponse> result =
                transactionService.getMyTransactions(
                        "test@test.com",
                        null,
                        0,
                        10
                );

        assertEquals(1, result.getTotalElements());
        assertSame(response, result.getContent().getFirst());
    }

    @Test
    void getMyTransactions_ShouldFilterByTransactionType() {

        User user = new User();
        user.setEmail("test@test.com");

        BankAccount account = new BankAccount();

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(bankAccountRepository.findByUser(user))
                .thenReturn(List.of(account));

        when(transactionRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(Page.empty());

        transactionService.getMyTransactions(
                "test@test.com",
                TransactionType.DEPOSIT,
                0,
                10
        );

        verify(transactionRepository).findAll(
                any(Specification.class),
                any(Pageable.class)
        );
    }

    @Test
    void getTransactions_ShouldThrow_WhenAccountDoesNotExist() {

        User user = new User();
        user.setEmail("test@test.com");

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(bankAccountRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                BankAccountNotFoundException.class,
                () -> transactionService.getTransactions(
                        1L,
                        "test@test.com",
                        0,
                        10,
                        null,
                        null,
                        null
                )
        );
    }

    @Test
    void getTransactions_ShouldThrow_WhenUserDoesNotOwnAccount() {

        User owner = new User();
        owner.setId(1L);

        User anotherUser = new User();
        anotherUser.setId(2L);

        BankAccount account = new BankAccount();
        account.setUser(owner);

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(anotherUser));

        when(bankAccountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        assertThrows(
                UnauthorizedAccountAccessException.class,
                () -> transactionService.getTransactions(
                        1L,
                        "test@test.com",
                        0,
                        10,
                        null,
                        null,
                        null
                )
        );
    }

    @Test
    void getTransactions_ShouldReturnTransactions() {

        User user = new User();
        user.setId(1L);

        BankAccount account = new BankAccount();
        account.setUser(user);

        Transaction transaction = new Transaction();

        TransactionResponse response = new TransactionResponse();

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(bankAccountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        when(transactionRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(transaction)));

        when(transactionMapper.toResponse(transaction))
                .thenReturn(response);

        Page<TransactionResponse> result =
                transactionService.getTransactions(
                        1L,
                        "test@test.com",
                        0,
                        10,
                        null,
                        null,
                        null
                );

        assertEquals(1, result.getTotalElements());
        assertSame(response, result.getContent().getFirst());
    }

}