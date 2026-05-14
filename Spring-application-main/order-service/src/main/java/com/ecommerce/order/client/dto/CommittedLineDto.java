package com.ecommerce.order.client.dto;

import java.math.BigDecimal;

public record CommittedLineDto(Long productId, String productName, BigDecimal unitPrice, int quantity) {}
