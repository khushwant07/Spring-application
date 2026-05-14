package com.ecommerce.product.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DecrementStockRequest(@NotNull @Min(1) Integer quantity) {}
