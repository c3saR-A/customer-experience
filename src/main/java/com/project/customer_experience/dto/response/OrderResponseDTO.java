package com.project.customer_experience.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.customer_experience.dto.OrderItemDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        @JsonProperty("id") // ESTO ES LO QUE FALTA
        Long id,

        @JsonProperty("clientId")
        Long clientId,

        @JsonProperty("clientEmail")
        String clientEmail,

        @JsonProperty("orderStatus")
        String orderStatus,

        @JsonProperty("datePaid")
        LocalDateTime datePaid,

        @JsonProperty("total")
        BigDecimal total,

        @JsonProperty("orderItems")
        List<OrderItemDTO> orderItems
) {}