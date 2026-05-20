package com.project.customer_experience.dto;

import java.math.BigDecimal;

public record OrderItemDTO(
        Long id,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal,
        String productName,
        String productSku
) {}
