package com.group12.ecommerce.service.interfaceService.product;

import com.group12.ecommerce.dto.request.product.ProductCreationRequest;
import com.group12.ecommerce.dto.request.product.ProductUpdateRequest;
import com.group12.ecommerce.dto.response.page.CustomPageResponse;
import com.group12.ecommerce.dto.response.product.ProductResponse;
import com.group12.ecommerce.dto.response.user.UserResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductService {
    ProductResponse createProduct(ProductCreationRequest request);
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Long id);
    ProductResponse updateProduct(Long id, ProductUpdateRequest request);
    void deleteProduct(Long id);

    // pageable
    CustomPageResponse<ProductResponse> getAllProductsWithPage(Pageable pageable);

    // search
    CustomPageResponse<ProductResponse> searchProducts(String name, Double minPrice,
                                                       Double maxPrice, Long categoryId, Pageable pageable);
}
