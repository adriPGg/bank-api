package com.adrian.bankapi.controller;

import com.adrian.bankapi.dto.TransferRequest;
import com.adrian.bankapi.service.TransactionService;
import com.adrian.bankapi.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.adrian.bankapi.dto.TransferResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/transfers")
@Tag(
        name = "Transfers",
        description = "Operaciones de transferencia entre cuentas bancarias"
)
@SecurityRequirement(name = "bearerAuth")
public class TransferController {

    private final TransferService transferService;
    private final TransactionService transactionService;

    public TransferController(
            TransferService transferService,
            TransactionService transactionService) {

        this.transferService = transferService;
        this.transactionService = transactionService;
    }

    @Operation(
            summary = "Realizar una transferencia",
            description = "Transfiere dinero desde una cuenta del usuario autenticado hacia otra cuenta bancaria."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transferencia realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Saldo insuficiente o datos inválidos"),
            @ApiResponse(responseCode = "403", description = "La cuenta origen no pertenece al usuario"),
            @ApiResponse(responseCode = "404", description = "Cuenta origen o destino no encontrada")
    })
    @PostMapping
    public TransferResponse transfer(
            @Valid @RequestBody TransferRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        return transferService.transfer(request, email);
    }

}