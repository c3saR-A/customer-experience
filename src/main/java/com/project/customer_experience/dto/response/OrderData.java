package com.project.customer_experience.dto.response;

import java.util.List;

public record OrderData(
        List<OrderResponseDTO> content

) {}