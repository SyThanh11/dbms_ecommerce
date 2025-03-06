package com.group12.ecommerce.controller.product;

import com.group12.ecommerce.dto.request.product.ProductCreationRequest;
import com.group12.ecommerce.dto.request.product.ProductUpdateRequest;
import com.group12.ecommerce.dto.response.api.ApiResponse;
import com.group12.ecommerce.dto.response.page.CustomPageResponse;
import com.group12.ecommerce.dto.response.product.ProductResponse;
import com.group12.ecommerce.service.interfaceService.product.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Product Management")
public class ProductController {
    @Autowired
    IProductService productService;

    @PostMapping
    ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody ProductCreationRequest request){
        ProductResponse productResponse = productService.createProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ProductResponse>builder()
                        .message("Create product success!")
                        .data(productResponse)
                        .build()
        );
    }

    @GetMapping
    ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts(){
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<List<ProductResponse>>builder()
                        .message("Get all products success!")
                        .data(
                                productService.getAllProducts()
                        )
                        .build()
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<ProductResponse>builder()
                        .message("Get product success!")
                        .data(
                                productService.getProductById(id)
                        )
                        .build()
        );
    }

    @PutMapping
    ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@RequestParam Long id,
                                                         @RequestBody ProductUpdateRequest request){
        ProductResponse productResponse = productService.updateProduct(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<ProductResponse>builder()
                        .message("Update product success")
                        .data(productResponse)
                        .build()
        );
    }

    @DeleteMapping
    ResponseEntity<ApiResponse<?>> deleteProduct(@RequestParam Long id){
        productService.deleteProduct(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .message("Delete product success!")
                        .build()
        );
    }

    @Operation(summary = "Get all products with paging", description = "API for getting all products with paging")
    @GetMapping("/get-page")
    ResponseEntity<ApiResponse<CustomPageResponse<ProductResponse>>> getAllProductsWithPage(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Pageable pageable;

        if (sort.isEmpty()) {
            pageable = PageRequest.of(page, size);
        } else {
            Sort.Direction sortDirection =
                    direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Sort sortBy = Sort.by(sortDirection, sort);
            pageable = PageRequest.of(page, size, sortBy);
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<CustomPageResponse<ProductResponse>>builder()
                        .message("Get all products success!")
                        .data(productService.getAllProductsWithPage(pageable))
                        .build()
        );
    }

    @Operation(summary = "Search products with filters", description = "API for searching products with filters")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<CustomPageResponse<ProductResponse>>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Long categoryId,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort.Direction sortDirection =
                direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        CustomPageResponse<ProductResponse> products = productService
                .searchProducts(name, minPrice, maxPrice, categoryId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<CustomPageResponse<ProductResponse>>builder()
                        .message("Search products success")
                        .data(products)
                        .build()
        );
    }
}
