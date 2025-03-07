package com.group12.ecommerce.controller.category;

import com.group12.ecommerce.dto.request.category.CategoryCreationRequest;
import com.group12.ecommerce.dto.request.category.CategoryUpdateRequest;
import com.group12.ecommerce.dto.request.permission.PermissionCreationRequest;
import com.group12.ecommerce.dto.request.permission.PermissionUpdateRequest;
import com.group12.ecommerce.dto.response.api.ApiResponse;
import com.group12.ecommerce.dto.response.category.CategoryResponse;
import com.group12.ecommerce.dto.response.page.CustomPageResponse;
import com.group12.ecommerce.dto.response.permission.PermissionResponse;
import com.group12.ecommerce.dto.response.user.UserResponse;
import com.group12.ecommerce.service.interfaceService.category.ICategoryService;
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
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Category Management")
public class CategoryController {
    @Autowired
    ICategoryService categoryService;

    @PostMapping
    ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@RequestBody CategoryCreationRequest request){
        CategoryResponse categoryResponse = categoryService.createCategory(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<CategoryResponse>builder()
                        .message("Create category success!")
                        .data(categoryResponse)
                        .build()
        );
    }

    @GetMapping
    ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(){
        List<CategoryResponse> responseList = categoryService.getAllCategories();

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<List<CategoryResponse>>builder()
                        .message("Get all categories success!")
                        .data(responseList)
                        .build()
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id){
        CategoryResponse categoryResponse = categoryService.getCategoryById(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<CategoryResponse>builder()
                        .message("Get permission success!")
                        .data(categoryResponse)
                        .build()
        );
    }

    @PutMapping
    ResponseEntity<ApiResponse<CategoryResponse>> updatePermission(
            @RequestParam Long id, @RequestBody CategoryUpdateRequest request){
        CategoryResponse categoryResponse = categoryService.updateCategory(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<CategoryResponse>builder()
                        .message("Update category success!")
                        .data(categoryResponse)
                        .build()
        );
    }

    @DeleteMapping
    ResponseEntity<ApiResponse> deleteCategory(@RequestParam Long id){
        categoryService.deleteCategory(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .message("Delete success!")
                        .build()
        );
    }

    @Operation(summary = "Get all categories with paging", description = "API for getting all categories with paging")
    @GetMapping("/get-page")
    ResponseEntity<ApiResponse<CustomPageResponse<CategoryResponse>>> getAllCategoriesWithPage(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Pageable pageable;

        if (sort.isEmpty()) {
            pageable = PageRequest.of(page, size);
        } else {
            Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Sort sortBy = Sort.by(sortDirection, sort);
            pageable = PageRequest.of(page, size, sortBy);
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<CustomPageResponse<CategoryResponse>>builder()
                        .message("Get all categories success!")
                        .data(categoryService.getAllCategoriesWithPage(pageable))
                        .build()
        );
    }

    @Operation(summary = "Search all categories with filter condition",
            description = "API for search all categories with filter condition")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<CustomPageResponse<CategoryResponse>>> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(defaultValue = "desc") String direction
    ){
        Pageable pageable;

        if (sort.isEmpty()) {
            pageable = PageRequest.of(page, size);
        } else {
            Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Sort sortBy = Sort.by(sortDirection, sort);
            pageable = PageRequest.of(page, size, sortBy);
        }

        CustomPageResponse<CategoryResponse> categories = categoryService.searchCategory(name, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<CustomPageResponse<CategoryResponse>>builder()
                        .message("Search users success")
                        .data(categories)
                        .build()
        );
    }
}
