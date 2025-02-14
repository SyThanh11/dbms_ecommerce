package com.group12.ecommerce.service.interfaceService.product;

import com.group12.ecommerce.dto.request.product.ProductCreationRequest;
import com.group12.ecommerce.dto.request.product.ProductUpdateRequest;
import com.group12.ecommerce.dto.response.product.ProductResponse;

import java.util.List;

public interface IProductService {
    ProductResponse createProduct(ProductCreationRequest request);
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Long id);
    ProductResponse updateProduct(Long id, ProductUpdateRequest request);
    void deleteProduct(Long id);
}
