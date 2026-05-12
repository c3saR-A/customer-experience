package com.project.customer_experience.dto;

public record OrderItemDTO(
        String sku,
        String name,
        int quantity,
        Double price
) {}
