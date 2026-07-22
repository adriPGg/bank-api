package com.adrian.bankapi.controller;

import com.adrian.bankapi.dto.TransactionResponse;
import com.adrian.bankapi.entity.TransactionType;
import com.adrian.bankapi.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public Page<TransactionResponse> getMyTransactions(

            Authentication authentication,

            @RequestParam(required = false)
            TransactionType type,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size) {

        String email = authentication.getName();

        return transactionService.getMyTransactions(
                email,
                type,
                page,
                size);
    }

    @GetMapping("/account/{id}")
    public Page<TransactionResponse> getTransactionsByAccount(

            @PathVariable Long id,

            Authentication authentication,

            @RequestParam(required =false)
            TransactionType type,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to) {

        String email = authentication.getName();

        return transactionService.getTransactions(
                id,
                email,
                page,
                size,
                type,
                from,
                to
        );
    }
}