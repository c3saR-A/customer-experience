package com.project.customer_experience.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderRequestDTO(
        @NotNull Long clientId, // De imagen_f81d90.png
        @Email @NotEmpty String clientEmail,
        @NotNull List<OrderItemRequestDTO> items
) {}