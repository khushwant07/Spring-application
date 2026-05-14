package com.ecommerce.product.web.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        String imageUrl,
        Long categoryId,
        String categoryName,
        Instant createdAt) {}
