package com.adrian.bankapi.repository;

import com.adrian.bankapi.entity.BankAccount;
import com.adrian.bankapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BankAccountRepository
        extends JpaRepository<BankAccount, Long> {

    List<BankAccount> findByUser(User user);

}
