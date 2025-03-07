package com.group12.ecommerce.service.interfaceService.category;

import com.group12.ecommerce.dto.request.category.CategoryCreationRequest;
import com.group12.ecommerce.dto.request.category.CategoryUpdateRequest;
import com.group12.ecommerce.dto.response.category.CategoryResponse;
import com.group12.ecommerce.dto.response.page.CustomPageResponse;
import com.group12.ecommerce.dto.response.user.UserResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICategoryService {
    CategoryResponse createCategory(CategoryCreationRequest request);
    CategoryResponse getCategoryById(Long id);
    List<CategoryResponse> getAllCategories();
    void deleteCategory(Long id);
    CategoryResponse updateCategory(Long id, CategoryUpdateRequest request);

    // pageable
    CustomPageResponse<CategoryResponse> getAllCategoriesWithPage(Pageable pageable);

    // search
    CustomPageResponse<CategoryResponse> searchCategory(String name, Pageable pageable);
}
