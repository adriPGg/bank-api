package com.adrian.bankapi.dto;

import com.adrian.bankapi.entity.AccountType;
import jakarta.validation.constraints.NotNull;

public class BankAccountRequest {

    @NotNull
    private AccountType accountType;

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
}

