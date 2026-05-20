package com.project.customer_experience.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderResponseDTO(
        Long id,
        String clientEmail,
        BigDecimal total,
        String orderStatus
) {}