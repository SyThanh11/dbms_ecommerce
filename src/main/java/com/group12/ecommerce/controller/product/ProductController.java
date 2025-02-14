package com.group12.ecommerce.controller.product;

import com.group12.ecommerce.dto.request.product.ProductCreationRequest;
import com.group12.ecommerce.dto.request.product.ProductUpdateRequest;
import com.group12.ecommerce.dto.response.api.ApiResponse;
import com.group12.ecommerce.dto.response.product.ProductResponse;
import com.group12.ecommerce.service.interfaceService.product.IProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
}
