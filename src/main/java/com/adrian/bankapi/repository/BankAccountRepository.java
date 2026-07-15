package com.adrian.bankapi.repository;

import com.adrian.bankapi.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository
        extends JpaRepository<BankAccount, Long> {

}
