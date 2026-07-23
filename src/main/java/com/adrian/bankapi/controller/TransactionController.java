package com.adrian.bankapi.controller;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(
        name = "Transactions",
        description = "Operaciones relacionadas con las transacciones bancarias"
)
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(
            summary = "Obtener mis transacciones",
            description = "Devuelve las transacciones del usuario autenticado con filtros opcionales."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping
    public Page<TransactionResponse> getMyTransactions(

            Authentication authentication,

            @Parameter(description = "Filtrar por tipo de transacción")
            @RequestParam(required = false)
            TransactionType type,

            @Parameter(description = "Fecha inicial del filtro")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @Parameter(description = "Fecha final del filtro")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to,

            @Parameter(description = "Número de página")
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(description = "Número de elementos por página")
            @RequestParam(defaultValue = "20")
            int size) {

        String email = authentication.getName();

        return transactionService.getMyTransactions(
                email,
                type,
                page,
                size);
    }

    @Operation(
            summary = "Obtener transacciones de una cuenta",
            description = "Devuelve las transacciones de una cuenta concreta del usuario autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
            @ApiResponse(responseCode = "403", description = "La cuenta no pertenece al usuario"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @GetMapping("/account/{id}")
    public Page<TransactionResponse> getTransactionsByAccount(

            @Parameter(description = "Identificador de la cuenta bancaria")
            @PathVariable Long id,

            Authentication authentication,

            @Parameter(description = "Filtrar por tipo de transacción")
            @RequestParam(required =false)
            TransactionType type,

            @Parameter(description = "Número de página")
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(description = "Número de elementos por página")
            @RequestParam(defaultValue = "20")
            int size,

            @Parameter(description = "Fecha inicial del filtro")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,

            @Parameter(description = "Fecha final del filtro")
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