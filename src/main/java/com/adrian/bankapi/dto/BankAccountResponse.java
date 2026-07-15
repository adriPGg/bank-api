package com.adrian.bankapi.dto;

import com.adrian.bankapi.entity.AccountType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class BankAccountResponse {

    private Long id;

    private String iban;

    private BigDecimal balance;

    private AccountType accountType;

    private LocalDateTime createdAt;

}
