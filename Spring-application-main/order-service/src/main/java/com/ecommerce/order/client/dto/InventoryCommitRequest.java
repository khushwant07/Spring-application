package com.ecommerce.order.client.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record InventoryCommitRequest(@NotEmpty @Valid List<InventoryCommitLine> lines) {}
