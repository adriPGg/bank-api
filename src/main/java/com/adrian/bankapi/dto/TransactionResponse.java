package com.adrian.bankapi.dto;

import com.adrian.bankapi.entity.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionResponse {

    private Long id;

    private BigDecimal amount;

    private TransactionType transactionType;

    private LocalDateTime createdAt;

    private Long fromAccountId;

    private Long toAccountId;
}