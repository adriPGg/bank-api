package com.adrian.bankapi.service;

import com.adrian.bankapi.entity.Transaction;
import com.adrian.bankapi.entity.TransactionType;

import java.time.LocalDateTime;

import com.adrian.bankapi.dto.TransferRequest;
import com.adrian.bankapi.dto.TransferResponse;
import com.adrian.bankapi.entity.BankAccount;
import com.adrian.bankapi.entity.User;
import com.adrian.bankapi.exception.BankAccountNotFoundException;
import com.adrian.bankapi.exception.InsufficientBalanceException;
import com.adrian.bankapi.exception.UnauthorizedAccountAccessException;
import com.adrian.bankapi.exception.UserNotFoundException;
import org.springframework.stereotype.Service;
import com.adrian.bankapi.repository.BankAccountRepository;
import com.adrian.bankapi.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import com.adrian.bankapi.repository.TransactionRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.adrian.bankapi.dto.TransactionResponse;


@Service
public class TransferService {

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public TransferService(
            BankAccountRepository bankAccountRepository,
            UserRepository userRepository,
            TransactionRepository transactionRepository) {

        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;

    }

    @Transactional
    public TransferResponse transfer(
            TransferRequest request,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("Usuario no encontrado"));

        BankAccount fromAccount = bankAccountRepository.findById(request.getFromAccountId())
                .orElseThrow(() ->
                        new BankAccountNotFoundException("Cuenta origen no encontrada"));

        BankAccount toAccount = bankAccountRepository.findById(request.getToAccountId())
                .orElseThrow(() ->
                        new BankAccountNotFoundException("Cuenta destino no encontrada"));

        if (!fromAccount.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccountAccessException(
                    "No tienes acceso a esta cuenta");
        }

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente");
        }

        fromAccount.setBalance(
                fromAccount.getBalance().subtract(request.getAmount())
        );

        toAccount.setBalance(
                toAccount.getBalance().add(request.getAmount())
        );

        bankAccountRepository.save(fromAccount);
        bankAccountRepository.save(toAccount);

        Transaction transaction = new Transaction();

        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(request.getAmount());
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);


        return new TransferResponse("Transferencia realizada correctamente");
    }

    public Page<TransactionResponse> getTransactions(
            Long accountId,
            String email,
            int page,
            int size,
            TransactionType type) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );
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

        Page<Transaction> transactionPage;

        if (type == null) {

            transactionPage = transactionRepository.findByFromAccountOrToAccount(
                    account,
                    account,
                    pageable
            );

        } else {

            transactionPage = transactionRepository
                    .findByTransactionTypeAndFromAccountOrTransactionTypeAndToAccount(
                            type,
                            account,
                            type,
                            account,
                            pageable
                    );
        }

        return transactionPage.map(transaction -> {

            TransactionResponse dto = new TransactionResponse();

            dto.setId(transaction.getId());
            dto.setAmount(transaction.getAmount());
            dto.setTransactionType(transaction.getTransactionType());
            dto.setCreatedAt(transaction.getCreatedAt());

            if (transaction.getFromAccount() != null) {
                dto.setFromAccountId(transaction.getFromAccount().getId());
            }

            if (transaction.getToAccount() != null) {
                dto.setToAccountId(transaction.getToAccount().getId());
            }

            return dto;
        });
    }

}