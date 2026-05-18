package com.ecommerce.order.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        Long userId,
        BigDecimal totalAmount,
        String status,
        Instant createdAt,
        List<OrderLineResponse> lines) {

    public record OrderLineResponse(
            Long productId, String productName, BigDecimal unitPrice, int quantity) {}
}
