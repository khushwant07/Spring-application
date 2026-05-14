package com.ecommerce.order.client.dto;

import java.util.List;

public record InventoryCommitResponse(List<CommittedLineDto> lines) {}
