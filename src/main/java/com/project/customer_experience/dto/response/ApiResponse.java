package com.project.customer_experience.dto.response;

public record ApiResponse(
        boolean success,
        String message,
        OrderData data
) {}