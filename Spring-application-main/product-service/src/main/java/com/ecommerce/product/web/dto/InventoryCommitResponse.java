package com.ecommerce.product.web.dto;

import java.util.List;

public record InventoryCommitResponse(List<CommittedLineResponse> lines) {}
