package com.group12.ecommerce.service.implementService.category;

import com.group12.ecommerce.base.exception.AppException;
import com.group12.ecommerce.base.exception.ErrorCode;
import com.group12.ecommerce.dto.request.category.CategoryCreationRequest;
import com.group12.ecommerce.dto.request.category.CategoryUpdateRequest;
import com.group12.ecommerce.dto.response.category.CategoryResponse;
import com.group12.ecommerce.entity.category.CategoryEntity;
import com.group12.ecommerce.mapper.category.ICategoryMapper;
import com.group12.ecommerce.repository.category.ICategoryRepository;
import com.group12.ecommerce.service.interfaceService.category.ICategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService implements ICategoryService {
    @Autowired
    ICategoryRepository categoryRepository;
    @Autowired
    ICategoryMapper categoryMapper;

    @Override
    public CategoryResponse createCategory(CategoryCreationRequest request) {
        try {
            CategoryEntity category = categoryMapper.toCategoryEntity(request);

            return categoryMapper.toCategoryResponse(
                    categoryRepository.save(category)
            );
        } catch(DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.NAME_CATEGORY_EXISTED);
        }
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(()
                        -> new AppException(ErrorCode.NAME_CATEGORY_NOT_EXISTED));
        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryMapper.toListCategoryResponse(
                categoryRepository.findAll()
        );
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryUpdateRequest request) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(()
                        -> new AppException(ErrorCode.NAME_CATEGORY_NOT_EXISTED));

        categoryMapper.updateCategoryEntity(category, request);

        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }
}
