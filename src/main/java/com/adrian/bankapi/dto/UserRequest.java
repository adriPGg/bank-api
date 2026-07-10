package com.adrian.bankapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8,message = "La contraseña debe de tener al menos 8 caracteres")
    private String password;

    @NotBlank(message = "El teléfono es obligatorio")
    private String phoneNumber;

    @Past(message = "La fecha de nacimiento debe de ser anterior a hoy")
    private LocalDate birthDate;

}
