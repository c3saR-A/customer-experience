package com.project.customer_experience.dto.response;


public record LoginResponseDTO(
        String accessToken,
        String refreshToken,
        String username
) {}
