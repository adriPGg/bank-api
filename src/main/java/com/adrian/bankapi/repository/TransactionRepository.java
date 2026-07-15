package com.adrian.bankapi.repository;

import com.adrian.bankapi.entity.BankAccount;

import java.util.List;

import com.adrian.bankapi.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByFromAccountOrToAccount(
            BankAccount fromAccount,
            BankAccount toAccount,
            Pageable pageable);
}