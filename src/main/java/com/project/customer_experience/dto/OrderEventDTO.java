package com.project.customer_experience.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderEventDTO {
    private String orderId;
    private String clientId;
    private String clientEmail;
    private List<ProductItem> products;
    private BigDecimal total;
    private String createdAt;

    @Data
    public static class ProductItem {
        private String sku;
        private String name;
        private int qty;
        private BigDecimal price;
    }
}
