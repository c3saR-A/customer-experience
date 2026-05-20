package com.project.customer_experience.dto.response;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data
) {}