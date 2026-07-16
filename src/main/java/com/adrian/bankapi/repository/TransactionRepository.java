package com.adrian.bankapi.repository;

import com.adrian.bankapi.entity.BankAccount;
import com.adrian.bankapi.entity.Transaction;
import com.adrian.bankapi.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long>,
        JpaSpecificationExecutor<Transaction> {

    Page<Transaction> findByFromAccountOrToAccount(
            BankAccount fromAccount,
            BankAccount toAccount,
            Pageable pageable);

    Page<Transaction> findByCreatedAtBetweenAndFromAccountOrCreatedAtBetweenAndToAccount(
            LocalDateTime fromDate1,
            LocalDateTime toDate1,
            BankAccount fromAccount,
            LocalDateTime fromDate2,
            LocalDateTime toDate2,
            BankAccount toAccount,
            Pageable pageable);
}