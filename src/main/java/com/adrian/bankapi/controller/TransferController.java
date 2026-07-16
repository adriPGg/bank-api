package com.adrian.bankapi.controller;

import com.adrian.bankapi.entity.TransactionType;
import com.adrian.bankapi.dto.TransferRequest;
import com.adrian.bankapi.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.adrian.bankapi.dto.TransferResponse;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import com.adrian.bankapi.dto.TransactionResponse;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public TransferResponse transfer(
            @Valid @RequestBody TransferRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        return transferService.transfer(request, email);
    }

    @GetMapping("/accounts/{id}/transactions")
    public Page<TransactionResponse> getTransactions(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false)
            @org.springframework.format.annotation.DateTimeFormat(
                    iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to,

            Authentication authentication) {

        String email = authentication.getName();

        return transferService.getTransactions(
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