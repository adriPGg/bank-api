package com.adrian.bankapi.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.adrian.bankapi.dto.BankAccountRequest;
import com.adrian.bankapi.dto.BankAccountResponse;

import com.adrian.bankapi.entity.BankAccount;
import com.adrian.bankapi.entity.User;

import com.adrian.bankapi.exception.UserNotFoundException;

import com.adrian.bankapi.repository.BankAccountRepository;
import com.adrian.bankapi.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository,
                              UserRepository userRepository) {

        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
    }

    public BankAccountResponse createAccount(
            BankAccountRequest request,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        BankAccount account = new BankAccount();

        account.setIban(generateIban());
        account.setBalance(BigDecimal.ZERO);
        account.setAccountType(request.getAccountType());
        account.setCreatedAt(LocalDateTime.now());
        account.setUser(user);
        BankAccount savedAccount = bankAccountRepository.save(account);

        return mapToResponse(savedAccount);
    }
    private String generateIban() {

        Random random = new Random();

        int checkDigits = random.nextInt(90) + 10;

        StringBuilder iban = new StringBuilder("ES" + checkDigits);

        for (int i = 0; i < 20; i++) {
            iban.append(random.nextInt(10));
        }

        return iban.toString();
    }
    private BankAccountResponse mapToResponse(BankAccount account) {

        BankAccountResponse response = new BankAccountResponse();

        response.setId(account.getId());
        response.setIban(account.getIban());
        response.setBalance(account.getBalance());
        response.setAccountType(account.getAccountType());
        response.setCreatedAt(account.getCreatedAt());

        return response;
    }
}