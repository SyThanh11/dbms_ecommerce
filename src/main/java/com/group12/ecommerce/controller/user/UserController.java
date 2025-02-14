package com.group12.ecommerce.controller.user;

import com.group12.ecommerce.dto.request.user.UserUpdateRequest;
import com.group12.ecommerce.dto.response.api.ApiResponse;
import com.group12.ecommerce.dto.response.page.CustomPageResponse;
import com.group12.ecommerce.dto.response.user.UserResponse;
import com.group12.ecommerce.service.interfaceService.user.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "User Management")
public class UserController {
    @Autowired
    IUserService userService;

    @Operation(summary = "Get all users", description = "Api for get all users")
    @GetMapping
    ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(){
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<List<UserResponse>>builder()
                        .message("Get all user success")
                        .data(userService.getAllUsers())
                        .build()
        );
    }

    @Operation(summary = "Get user by id", description = "Api for get user by id")
    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String id){
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<UserResponse>builder()
                        .message("Get user success")
                        .data(userService.getUserById(id))
                        .build()
        );
    }

    @Operation(summary = "Delete user", description = "Api for delete user by id")
    @DeleteMapping
    ResponseEntity<ApiResponse<?>> deleteUser(@RequestParam String id){
        userService.deleteUser(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<UserResponse>builder()
                        .message("delete user success")
                        .build()
        );
    }

    @Operation(summary = "Update user", description = "Api for update user by id")
    @PutMapping
    ResponseEntity<ApiResponse<UserResponse>> updateUser(@RequestParam String id,
                         @RequestBody UserUpdateRequest request){
        UserResponse userResponse = userService.updateUser(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<UserResponse>builder()
                        .message("Update user success")
                        .data(userResponse)
                        .build()
        );
    }

    @Operation(summary = "Get all users with paging", description = "API for getting all users with paging")
    @GetMapping("/get-page")
    ResponseEntity<ApiResponse<CustomPageResponse<UserResponse>>> getAllUsersWithPage(
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
                ApiResponse.<CustomPageResponse<UserResponse>>builder()
                        .message("Get all users success!")
                        .data(userService.getAllUsersWithPage(pageable))
                        .build()
        );
    }

}
