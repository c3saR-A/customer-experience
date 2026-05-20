package com.project.customer_experience.dto;

import java.util.List;

public record OrderConfirmedEvent(
        String orderId,
        String clientId,
        String clientEmail,
        List<ProductItemRecord> products,
        Double total,
        String createdAt
) {}

record ProductItemRecord(
        String sku,
        String name,
        Integer qty,
        Double price
) {}