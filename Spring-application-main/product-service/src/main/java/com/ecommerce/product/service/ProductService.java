package com.ecommerce.product.service;

import com.ecommerce.product.domain.Category;
import com.ecommerce.product.domain.Product;
import com.ecommerce.product.repo.CategoryRepository;
import com.ecommerce.product.repo.ProductRepository;
import com.ecommerce.product.web.dto.CommittedLineResponse;
import com.ecommerce.product.web.dto.DecrementStockRequest;
import com.ecommerce.product.web.dto.InternalProductResponse;
import com.ecommerce.product.web.dto.InventoryCommitRequest;
import com.ecommerce.product.web.dto.InventoryCommitResponse;
import com.ecommerce.product.web.dto.InventoryUpdateRequest;
import com.ecommerce.product.web.dto.PagedResponse;
import com.ecommerce.product.web.dto.ProductRequest;
import com.ecommerce.product.web.dto.ProductResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> search(Long categoryId, String q, int page, int size) {
        String query = q == null || q.isBlank() ? null : q.trim();
        Page<Product> result =
                productRepository.search(
                        categoryId,
                        query,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        return new PagedResponse<>(
                result.getContent().stream().map(this::toResponse).toList(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize());
    }

    @Transactional(readOnly = true)
    public ProductResponse get(Long id) {
        return productRepository.findById(id).map(this::toResponse).orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    @Transactional(readOnly = true)
    public InternalProductResponse getInternal(Long id) {
        Product p =
                productRepository
                        .findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return new InternalProductResponse(
                p.getId(), p.getName(), p.getPrice(), p.getStockQuantity(), p.getCategoryId());
    }

    @Transactional
    public InventoryCommitResponse commitInventory(InventoryCommitRequest request) {
        Map<Long, Integer> merged = new LinkedHashMap<>();
        for (var line : request.lines()) {
            merged.merge(line.productId(), line.quantity(), Integer::sum);
        }
        List<CommittedLineResponse> committed = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : merged.entrySet()) {
            Long productId = entry.getKey();
            int qty = entry.getValue();
            int updated = productRepository.decrementStockIfEnough(productId, qty);
            if (updated == 0) {
                throw new IllegalStateException("Insufficient stock for product " + productId);
            }
            Product p =
                    productRepository
                            .findById(productId)
                            .orElseThrow(() -> new IllegalStateException("Product missing after update"));
            committed.add(new CommittedLineResponse(p.getId(), p.getName(), p.getPrice(), qty));
        }
        return new InventoryCommitResponse(committed);
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Category category =
                categoryRepository
                        .findById(request.categoryId())
                        .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        Product p = new Product();
        apply(p, request, category);
        return toResponse(productRepository.save(p));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product p =
                productRepository
                        .findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        Category category =
                categoryRepository
                        .findById(request.categoryId())
                        .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        apply(p, request, category);
        return toResponse(productRepository.save(p));
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found");
        }
        productRepository.deleteById(id);
    }

    @Transactional
    public ProductResponse updateInventory(Long id, InventoryUpdateRequest request) {
        Product p =
                productRepository
                        .findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        p.setStockQuantity(request.stockQuantity());
        return toResponse(productRepository.save(p));
    }

    @Transactional
    public void decrementStock(Long productId, DecrementStockRequest request) {
        int updated = productRepository.decrementStockIfEnough(productId, request.quantity());
        if (updated == 0) {
            throw new IllegalStateException("Insufficient stock for product " + productId);
        }
    }

    private void apply(Product p, ProductRequest request, Category category) {
        p.setName(request.name().trim());
        p.setDescription(request.description());
        p.setPrice(request.price());
        p.setStockQuantity(request.stockQuantity());
        p.setImageUrl(request.imageUrl());
        p.setCategory(category);
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStockQuantity(),
                p.getImageUrl(),
                p.getCategoryId(),
                p.getCategory().getName(),
                p.getCreatedAt());
    }
}
