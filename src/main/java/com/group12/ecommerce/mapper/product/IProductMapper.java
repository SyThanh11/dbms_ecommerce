package com.group12.ecommerce.mapper.product;

import com.group12.ecommerce.dto.request.product.ProductCreationRequest;
import com.group12.ecommerce.dto.request.product.ProductUpdateRequest;
import com.group12.ecommerce.dto.request.role.RoleCreationRequest;
import com.group12.ecommerce.dto.request.role.RoleUpdateRequest;
import com.group12.ecommerce.dto.response.product.ProductResponse;
import com.group12.ecommerce.dto.response.role.RoleResponse;
import com.group12.ecommerce.entity.product.ProductEntity;
import com.group12.ecommerce.entity.role.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IProductMapper {
    @Mapping(target = "categories", ignore = true)
    ProductEntity toProductEntity(ProductCreationRequest request);
    ProductResponse toProductResponse(ProductEntity productEntity);
    List<ProductResponse> toListProductResponse(List<ProductEntity> productEntities);
    @Mapping(target = "categories", ignore = true)
    void updateProductEntity(@MappingTarget ProductEntity productEntity, ProductUpdateRequest request);
}
