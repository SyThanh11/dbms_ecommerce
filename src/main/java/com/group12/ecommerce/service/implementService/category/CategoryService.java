package com.group12.ecommerce.service.implementService.category;

import com.group12.ecommerce.base.exception.AppException;
import com.group12.ecommerce.base.exception.ErrorCode;
import com.group12.ecommerce.dto.request.category.CategoryCreationRequest;
import com.group12.ecommerce.dto.request.category.CategoryUpdateRequest;
import com.group12.ecommerce.dto.response.category.CategoryResponse;
import com.group12.ecommerce.dto.response.page.CustomPageResponse;
import com.group12.ecommerce.dto.response.user.UserResponse;
import com.group12.ecommerce.entity.category.CategoryEntity;
import com.group12.ecommerce.entity.user.UserEntity;
import com.group12.ecommerce.mapper.category.ICategoryMapper;
import com.group12.ecommerce.repository.category.ICategoryRepository;
import com.group12.ecommerce.service.interfaceService.category.ICategoryService;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
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
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_USER')")
    public CategoryResponse getCategoryById(Long id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(()
                        -> new AppException(ErrorCode.NAME_CATEGORY_NOT_EXISTED));
        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_USER')")
    public List<CategoryResponse> getAllCategories() {
        return categoryMapper.toListCategoryResponse(
                categoryRepository.findAll()
        );
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CategoryResponse updateCategory(Long id, CategoryUpdateRequest request) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(()
                        -> new AppException(ErrorCode.NAME_CATEGORY_NOT_EXISTED));

        categoryMapper.updateCategoryEntity(category, request);

        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_USER')")
    public CustomPageResponse<CategoryResponse> getAllCategoriesWithPage(Pageable pageable) {
        Page<CategoryEntity> categoryEntities = categoryRepository.findAll(pageable);
        Page<CategoryResponse> categoryResponses = categoryEntities.map(categoryMapper::toCategoryResponse);
        return CustomPageResponse.fromPage(categoryResponses);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_USER')")
    public CustomPageResponse<CategoryResponse> searchCategory(String name, Pageable pageable) {
        Specification<CategoryEntity> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isBlank()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<CategoryEntity> categoryEntities = categoryRepository.findAll(spec, pageable);
        Page<CategoryResponse> categoryResponses = categoryEntities.map(categoryMapper::toCategoryResponse);
        return CustomPageResponse.fromPage(categoryResponses);
    }
}
