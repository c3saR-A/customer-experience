package com.project.customer_experience.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


public record LoginRequestDTO(
        @NotBlank(message = "El nombre de usuario es obligatorio")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {}
