package com.project.customer_experience.dto.request;

import java.util.List;

public record OrderRequestDTO(
        Long clientId,
        String clientEmail,
        List<OrderItemRequestDTO> items
) {}