package com.adrian.bankapi.service;

import com.adrian.bankapi.dto.TransactionResponse;
import com.adrian.bankapi.entity.BankAccount;
import com.adrian.bankapi.entity.Transaction;
import com.adrian.bankapi.entity.TransactionType;
import com.adrian.bankapi.entity.User;

import com.adrian.bankapi.exception.UserNotFoundException;

import com.adrian.bankapi.mapper.TransactionMapper;
import com.adrian.bankapi.repository.BankAccountRepository;
import com.adrian.bankapi.repository.TransactionRepository;
import com.adrian.bankapi.repository.UserRepository;

import com.adrian.bankapi.specification.TransactionSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import com.adrian.bankapi.exception.BankAccountNotFoundException;
import com.adrian.bankapi.exception.UnauthorizedAccountAccessException;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(
            TransactionRepository transactionRepository,
            UserRepository userRepository,
            BankAccountRepository bankAccountRepository,
            TransactionMapper transactionMapper) {

        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionMapper = transactionMapper;
    }

    public Page<TransactionResponse> getMyTransactions(
            String email,
            TransactionType type,
            int page,
            int size)
    {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("Usuario no encontrado"));

        List<BankAccount> accounts = bankAccountRepository.findByUserAndActiveTrue(user);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return searchTransactions(
                TransactionSpecification.filter(
                        accounts,
                        null,
                        type,
                        null,
                        null
                ),
                pageable
        );
    }

    public Page<TransactionResponse> getTransactions(
            Long accountId,
            String email,
            int page,
            int size,
            TransactionType type,
            LocalDateTime from,
            LocalDateTime to) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("Usuario no encontrado"));

        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() ->
                        new BankAccountNotFoundException("Cuenta no encontrada"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccountAccessException(
                    "No tienes acceso a esta cuenta");
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return searchTransactions(
                TransactionSpecification.filter(
                        null,
                        account,
                        type,
                        from,
                        to
                ),
                pageable
        );
    }

    private Page<TransactionResponse> searchTransactions(
            Specification<Transaction> specification,
            Pageable pageable) {

        return transactionRepository
                .findAll(specification, pageable)
                .map(transactionMapper::toResponse);
    }

}