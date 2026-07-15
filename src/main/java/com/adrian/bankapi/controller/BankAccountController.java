package com.adrian.bankapi.controller;

import com.adrian.bankapi.service.BankAccountService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adrian.bankapi.dto.BankAccountRequest;
import com.adrian.bankapi.dto.BankAccountResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }
    @PostMapping
    public BankAccountResponse createAccount(
            @Valid @RequestBody BankAccountRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        return bankAccountService.createAccount(request, email);
    }
}