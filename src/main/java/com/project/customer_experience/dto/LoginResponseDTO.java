package com.project.customer_experience.dto;


public record LoginResponseDTO(
        String accessToken,
        String refreshToken,
        String username
) {}
