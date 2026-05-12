package com.project.customer_experience.dto;

import java.math.BigDecimal;

public record ProductDataDTO(
        Long id,
        String name,
        BigDecimal regularPrice,
        int stockQuantity
) {}
