package com.project.customer_experience.dto;

import java.util.List;

public record OrderEventDTO(
        String orderId,
        String clientId,
        String clientEmail,
        List<OrderItemDTO> products,
        Double total
) {}
