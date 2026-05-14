package com.ecommerce.product.service;

import com.ecommerce.product.domain.Category;
import com.ecommerce.product.repo.CategoryRepository;
import com.ecommerce.product.web.dto.CategoryRequest;
import com.ecommerce.product.web.dto.CategoryResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> list() {
        return categoryRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse get(Long id) {
        return categoryRepository.findById(id).map(this::toResponse).orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new IllegalArgumentException("Category name already exists");
        }
        Category c = new Category();
        c.setName(request.name().trim());
        c.setDescription(request.description());
        return toResponse(categoryRepository.save(c));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category c =
                categoryRepository
                        .findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        c.setName(request.name().trim());
        c.setDescription(request.description());
        return toResponse(categoryRepository.save(c));
    }

    @Transactional
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Category not found");
        }
        categoryRepository.deleteById(id);
    }

    private CategoryResponse toResponse(Category c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getDescription());
    }
}
