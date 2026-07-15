package com.adrian.bankapi.controller;

import java.util.List;

import com.adrian.bankapi.dto.WithdrawRequest;
import com.adrian.bankapi.dto.BankAccountRequest;
import com.adrian.bankapi.dto.BankAccountResponse;
import com.adrian.bankapi.dto.DepositRequest;
import com.adrian.bankapi.service.BankAccountService;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public List<BankAccountResponse> getMyAccounts(Authentication authentication) {

        String email = authentication.getName();
        return bankAccountService.getMyAccounts(email);
    }

    @GetMapping("/{id}")
    public BankAccountResponse getAccountById(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        return bankAccountService.getAccountById(id, email);
    }

    @PostMapping("/{id}/deposit")
    public BankAccountResponse deposit(
            @PathVariable Long id,
            @Valid @RequestBody DepositRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        return bankAccountService.deposit(id, request, email);
    }

    @PostMapping("/{id}/withdraw")
    public BankAccountResponse withdraw(
            @PathVariable Long id,
            @Valid @RequestBody WithdrawRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        return bankAccountService.withdraw(id, request, email);
    }
}