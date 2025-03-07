package com.group12.ecommerce.service.implementService.product;

import com.group12.ecommerce.base.exception.AppException;
import com.group12.ecommerce.base.exception.ErrorCode;
import com.group12.ecommerce.dto.request.product.ProductCreationRequest;
import com.group12.ecommerce.dto.request.product.ProductUpdateRequest;
import com.group12.ecommerce.dto.response.page.CustomPageResponse;
import com.group12.ecommerce.dto.response.product.ProductResponse;
import com.group12.ecommerce.dto.response.user.UserResponse;
import com.group12.ecommerce.entity.category.CategoryEntity;
import com.group12.ecommerce.entity.product.ProductEntity;
import com.group12.ecommerce.entity.user.UserEntity;
import com.group12.ecommerce.mapper.product.IProductMapper;
import com.group12.ecommerce.repository.category.ICategoryRepository;
import com.group12.ecommerce.repository.product.IProductRepository;
import com.group12.ecommerce.service.interfaceService.product.IProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService implements IProductService {
    @Autowired
    IProductRepository productRepository;
    @Autowired
    ICategoryRepository categoryRepository;
    @Autowired
    IProductMapper productMapper;

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ProductResponse createProduct(ProductCreationRequest request) {
        try {
            ProductEntity productEntity = productMapper.toProductEntity(request);

            if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
                List<CategoryEntity> categoryEntities = categoryRepository
                        .findAllById(request.getCategoryIds());

                productEntity.setCategories(new HashSet<>(categoryEntities));
            } else {
                productEntity.setCategories(new HashSet<>());
            }

            return productMapper.toProductResponse(productRepository.save(productEntity));
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.NAME_PRODUCT_EXISTED);
        }
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_USER')")
    public List<ProductResponse> getAllProducts() {
        return productMapper.toListProductResponse(
                productRepository.findAll()
        );
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_USER')")
    public ProductResponse getProductById(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NAME_PRODUCT_NOT_EXISTED));

        return productMapper.toProductResponse(product);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NAME_PRODUCT_NOT_EXISTED));

        productMapper.updateProductEntity(product, request);

        List<CategoryEntity> categoryEntities = categoryRepository
                .findAllById(request.getCategoryIds());
        product.setCategories(new HashSet<>(categoryEntities));

        return productMapper.toProductResponse(productRepository.save(product));
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_USER')")
    public CustomPageResponse<ProductResponse> getAllProductsWithPage(Pageable pageable) {
        Page<ProductEntity> productEntities = productRepository.findAll(pageable);
        Page<ProductResponse> productResponses = productEntities.map(productMapper::toProductResponse);
        return CustomPageResponse.fromPage(productResponses);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_USER')")
    public CustomPageResponse<ProductResponse> searchProducts(String name, Double minPrice,
                                                              Double maxPrice, Long categoryId, Pageable pageable) {
        Page<ProductEntity> products = productRepository.searchProducts(name, minPrice, maxPrice, categoryId, pageable);
        Page<ProductResponse> productResponse = products.map(productMapper::toProductResponse);
        return CustomPageResponse.fromPage(productResponse);
    }
}
