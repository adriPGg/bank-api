package com.adrian.bankapi.controller;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;


import com.adrian.bankapi.dto.WithdrawRequest;
import com.adrian.bankapi.dto.BankAccountRequest;
import com.adrian.bankapi.dto.BankAccountResponse;
import com.adrian.bankapi.dto.DepositRequest;
import com.adrian.bankapi.service.BankAccountService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@Tag(
        name = "Bank Accounts",
        description = "Operaciones relacionadas con las cuentas bancarias"
)
@SecurityRequirement(name = "bearerAuth")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @Operation(
            summary = "Crear cuenta bancaria",
            description = "Crea una nueva cuenta bancaria para el usuario autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping
    public BankAccountResponse createAccount(
            @Valid @RequestBody BankAccountRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        return bankAccountService.createAccount(request, email);
    }

    @Operation(
            summary = "Obtener mis cuentas",
            description = "Devuelve todas las cuentas bancarias activas del usuario autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping
    public List<BankAccountResponse> getMyAccounts(Authentication authentication) {

        String email = authentication.getName();
        return bankAccountService.getMyAccounts(email);
    }

    @Operation(
            summary = "Obtener cuenta por ID",
            description = "Devuelve una cuenta bancaria concreta del usuario autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta encontrada"),
            @ApiResponse(responseCode = "403", description = "La cuenta no pertenece al usuario"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @GetMapping("/{id}")
    public BankAccountResponse getAccountById(
            @Parameter(description = "Identificador de la cuenta")
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        return bankAccountService.getAccountById(id, email);
    }

    @Operation(
            summary = "Realizar un ingreso",
            description = "Añade saldo a una cuenta bancaria."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ingreso realizado correctamente"),
            @ApiResponse(responseCode = "403", description = "La cuenta no pertenece al usuario"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @PostMapping("/{id}/deposit")
    public BankAccountResponse deposit(
            @Parameter(description = "Identificador de la cuenta")
            @PathVariable Long id,
            @Valid @RequestBody DepositRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        return bankAccountService.deposit(id, request, email);
    }

    @Operation(
            summary = "Retirar dinero",
            description = "Retira saldo de una cuenta bancaria."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retirada realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Saldo insuficiente"),
            @ApiResponse(responseCode = "403", description = "La cuenta no pertenece al usuario"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @PostMapping("/{id}/withdraw")
    public BankAccountResponse withdraw(
            @Parameter(description = "Identificador de la cuenta")
            @PathVariable Long id,
            @Valid @RequestBody WithdrawRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        return bankAccountService.withdraw(id, request, email);

    }

    @Operation(
            summary = "Eliminar cuenta bancaria",
            description = "Desactiva una cuenta bancaria del usuario autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cuenta eliminada correctamente"),
            @ApiResponse(responseCode = "403", description = "La cuenta no pertenece al usuario"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(
            @Parameter(description = "Identificador de la cuenta")
            @PathVariable Long id,
            Authentication authentication) {

        bankAccountService.deleteAccount(id, authentication.getName());

        return ResponseEntity.noContent().build();
    }

}