package com.project.customer_experience.dto.response;

import com.project.customer_experience.dto.ProductDataDTO;

public record ProductResponseDTO(
        boolean success,
        String message,
        ProductDataDTO data // <--- El JSON del Grupo A tiene este nodo
) {}
