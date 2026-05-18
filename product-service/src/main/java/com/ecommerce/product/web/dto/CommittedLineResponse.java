package com.ecommerce.product.web.dto;

import java.math.BigDecimal;

public record CommittedLineResponse(
        Long productId, String productName, BigDecimal unitPrice, int quantity) {}
