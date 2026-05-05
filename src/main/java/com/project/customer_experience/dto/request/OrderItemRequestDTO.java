package com.project.customer_experience.dto.request;

public record OrderItemRequestDTO(
        Long productId,
        Integer quantity) {
}
