package com.adrian.bankapi.controller;

import com.adrian.bankapi.dto.TransferRequest;
import com.adrian.bankapi.service.TransactionService;
import com.adrian.bankapi.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.adrian.bankapi.dto.TransferResponse;

@RestController
@RequestMapping("/transfers")
public class TransferController {

    private final TransferService transferService;
    private final TransactionService transactionService;

    public TransferController(
            TransferService transferService,
            TransactionService transactionService) {

        this.transferService = transferService;
        this.transactionService = transactionService;
    }

    @PostMapping
    public TransferResponse transfer(
            @Valid @RequestBody TransferRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        return transferService.transfer(request, email);
    }

}