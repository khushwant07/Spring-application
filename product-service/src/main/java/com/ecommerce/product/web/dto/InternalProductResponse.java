package com.ecommerce.product.web.dto;

import java.math.BigDecimal;

public record InternalProductResponse(
        Long id, String name, BigDecimal price, int stockQuantity, Long categoryId) {}
