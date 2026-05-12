package com.project.customer_experience.dto.response;

import java.math.BigDecimal;

public record OrderResponseDTO(
        Long id,
        String clientEmail,
        BigDecimal total,
        String orderStatus
) {}