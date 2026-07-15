package com.adrian.bankapi.repository;

import com.adrian.bankapi.entity.BankAccount;

import java.util.List;
import com.adrian.bankapi.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    List<Transaction> findByFromAccountOrToAccount(
            BankAccount fromAccount,
            BankAccount toAccount);

}