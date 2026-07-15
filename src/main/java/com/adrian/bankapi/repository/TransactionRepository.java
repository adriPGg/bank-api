package com.adrian.bankapi.repository;

import com.adrian.bankapi.entity.BankAccount;

import java.util.List;

import com.adrian.bankapi.entity.Transaction;
import com.adrian.bankapi.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByFromAccountOrToAccount(
            BankAccount fromAccount,
            BankAccount toAccount,
            Pageable pageable);

    Page<Transaction> findByTransactionTypeAndFromAccountOrTransactionTypeAndToAccount(
            TransactionType transactionType,
            BankAccount fromAccount,
            TransactionType transactionType2,
            BankAccount toAccount,
            Pageable pageable);
}