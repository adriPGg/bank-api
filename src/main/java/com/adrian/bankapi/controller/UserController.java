package com.adrian.bankapi.controller;

import com.adrian.bankapi.dto.UserRequest;
import com.adrian.bankapi.dto.UserResponse;
import com.adrian.bankapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(
        name = "Users",
        description = "Operaciones relacionadas con los usuarios"
)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Registrar usuario",
            description = "Crea un nuevo usuario en el sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "El email ya existe")
    })
    @PostMapping
    public UserResponse createUser(
            @Valid @RequestBody UserRequest request) {
        return userService.createUser(request);
    }

    @Operation(
            summary = "Obtener usuario por ID",
            description = "Devuelve la información de un usuario."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public UserResponse getUserById(
            @Parameter(description = "Identificador del usuario")
            @PathVariable Long id) {
        return userService.getUserById(id);
    }

    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza los datos de un usuario existente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/{id}")
    public UserResponse updateUser(
            @Parameter(description = "Identificador del usuario")
            @PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return userService.updateUser(id, request);
    }

    @Operation(
            summary = "Obtener todos los usuarios",
            description = "Devuelve el listado de usuarios registrados."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    })
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina un usuario del sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(
            @Parameter(description = "Identificador del usuario")
            @PathVariable Long id) {

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

