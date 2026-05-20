package com.project.customer_experience.dto.response;

import com.project.customer_experience.dto.ProductDataDTO;

import java.math.BigDecimal;

public record ProductResponseDTO(
        Long id,
        String name,
        String sku,
        BigDecimal regularPrice,
        int stockQuantity
        // Puedes omitir descripción si no la necesitas en el carrito
) {}
