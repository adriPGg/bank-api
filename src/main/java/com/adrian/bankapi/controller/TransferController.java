package com.adrian.bankapi.controller;

import com.adrian.bankapi.dto.TransferRequest;
import com.adrian.bankapi.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.adrian.bankapi.dto.TransferResponse;
import org.springframework.data.domain.Page;
import java.util.List;

import com.adrian.bankapi.dto.TransactionResponse;

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
            Authentication authentication) {

        System.out.println("=== ENTRANDO EN GET TRANSACTIONS ===");

        String email = authentication.getName();

        return transferService.getTransactions(id, email, page, size);
    }
}