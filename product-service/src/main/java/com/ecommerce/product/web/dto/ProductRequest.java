package com.ecommerce.product.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 5000) String description,
        @NotNull @Min(0) BigDecimal price,
        @NotNull @Min(0) Integer stockQuantity,
        @Size(max = 1024) String imageUrl,
        @NotNull Long categoryId) {}
