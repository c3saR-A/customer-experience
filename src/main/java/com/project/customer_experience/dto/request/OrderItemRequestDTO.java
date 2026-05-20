package com.project.customer_experience.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequestDTO(
        @NotNull Long productId, // De imagen_f81d70.png
        @Min(1) int quantity
) {}