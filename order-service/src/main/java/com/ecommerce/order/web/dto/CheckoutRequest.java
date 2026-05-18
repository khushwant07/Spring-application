package com.ecommerce.order.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CheckoutRequest(@NotEmpty @Valid List<CheckoutLine> lines) {

    public record CheckoutLine(@NotNull Long productId, @NotNull @Min(1) Integer quantity) {}
}
