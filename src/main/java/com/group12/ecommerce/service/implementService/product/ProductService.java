package com.group12.ecommerce.service.implementService.product;

import com.group12.ecommerce.base.exception.AppException;
import com.group12.ecommerce.base.exception.ErrorCode;
import com.group12.ecommerce.dto.request.product.ProductCreationRequest;
import com.group12.ecommerce.dto.request.product.ProductUpdateRequest;
import com.group12.ecommerce.dto.response.product.ProductResponse;
import com.group12.ecommerce.entity.category.CategoryEntity;
import com.group12.ecommerce.entity.product.ProductEntity;
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
    public List<ProductResponse> getAllProducts() {
        return productMapper.toListProductResponse(
                productRepository.findAll()
        );
    }

    @Override
    public ProductResponse getProductById(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NAME_PRODUCT_NOT_EXISTED));

        return productMapper.toProductResponse(product);
    }

    @Override
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
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
