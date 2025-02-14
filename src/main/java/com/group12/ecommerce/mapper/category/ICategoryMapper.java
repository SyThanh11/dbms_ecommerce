package com.group12.ecommerce.mapper.category;

import com.group12.ecommerce.dto.request.category.CategoryCreationRequest;
import com.group12.ecommerce.dto.request.category.CategoryUpdateRequest;
import com.group12.ecommerce.dto.response.category.CategoryResponse;
import com.group12.ecommerce.entity.category.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ICategoryMapper {
    CategoryEntity toCategoryEntity(CategoryCreationRequest request);
    CategoryResponse toCategoryResponse(CategoryEntity category);
    List<CategoryResponse> toListCategoryResponse(List<CategoryEntity> categoryEntityList);
    void updateCategoryEntity(@MappingTarget CategoryEntity category, CategoryUpdateRequest request);
}
