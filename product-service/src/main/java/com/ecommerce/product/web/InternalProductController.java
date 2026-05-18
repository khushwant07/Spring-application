package com.ecommerce.product.web;

import com.ecommerce.product.service.ProductService;
import com.ecommerce.product.web.dto.DecrementStockRequest;
import com.ecommerce.product.web.dto.InternalProductResponse;
import com.ecommerce.product.web.dto.InventoryCommitRequest;
import com.ecommerce.product.web.dto.InventoryCommitResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class InternalProductController {

    private final ProductService productService;

    public InternalProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products/{id}")
    public InternalProductResponse get(@PathVariable Long id) {
        return productService.getInternal(id);
    }

    @PostMapping("/inventory/commit")
    public InventoryCommitResponse commit(@Valid @RequestBody InventoryCommitRequest request) {
        return productService.commitInventory(request);
    }

    @PostMapping("/products/{id}/decrement")
    public ResponseEntity<Void> decrement(
            @PathVariable Long id, @Valid @RequestBody DecrementStockRequest request) {
        productService.decrementStock(id, request);
        return ResponseEntity.noContent().build();
    }
}
