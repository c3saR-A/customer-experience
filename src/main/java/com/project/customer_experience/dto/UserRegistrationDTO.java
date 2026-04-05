package com.project.customer_experience.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDTO {
    @NotBlank(message = "El usuario es requerido")
    private String username;

    @Email(message = "Email no válido")
    @NotBlank(message = "El email es requerido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 8, message = "La contraseña debe tener una longitud mínima de 8 caracteres")
    private String password;

    private String firstname;
    private String lastname;
}
