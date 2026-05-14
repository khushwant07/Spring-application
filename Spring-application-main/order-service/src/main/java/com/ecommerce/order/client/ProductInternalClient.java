package com.ecommerce.order.client;

import com.ecommerce.order.client.dto.InventoryCommitRequest;
import com.ecommerce.order.client.dto.InventoryCommitResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-service", configuration = ProductFeignConfig.class)
public interface ProductInternalClient {

    @PostMapping("/internal/inventory/commit")
    InventoryCommitResponse commit(@RequestBody InventoryCommitRequest request);
}
