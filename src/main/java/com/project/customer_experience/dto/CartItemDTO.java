package com.project.customer_experience.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItemDTO(
        @NotNull Long productId,
        @Min(1) Integer quantity
) {}
