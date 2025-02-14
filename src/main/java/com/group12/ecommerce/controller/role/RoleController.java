package com.group12.ecommerce.controller.role;

import com.group12.ecommerce.dto.request.role.RoleCreationRequest;
import com.group12.ecommerce.dto.request.role.RoleUpdateRequest;
import com.group12.ecommerce.dto.response.api.ApiResponse;
import com.group12.ecommerce.dto.response.role.RoleResponse;
import com.group12.ecommerce.service.interfaceService.role.IRoleService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Role Management")
public class RoleController {
    @Autowired
    IRoleService roleService;

    @Operation(summary = "Create new role", description = "Api for create role")
    @PostMapping
    ResponseEntity<ApiResponse<RoleResponse>> createRole(@RequestBody RoleCreationRequest request){
        RoleResponse roleResponse = roleService.createRole(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<RoleResponse>builder()
                        .message("Create role success!")
                        .data(roleResponse)
                        .build()
        );
    }

    @Operation(summary = "Get role by id", description = "Api for get role by id")
    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<RoleResponse>> getRoleById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<RoleResponse>builder()
                        .message("Get role success!")
                        .data(
                                roleService.getRoleById(id)
                        )
                        .build()
        );
    }

    @Operation(summary = "Get all roles", description = "Api for get all roles")
    @GetMapping
    ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles(){
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<List<RoleResponse>>builder()
                        .message("Get role success!")
                        .data(
                                roleService.getAllRoles()
                        )
                        .build()
        );
    }

    @Operation(summary = "Update role", description = "Api for update role by id")
    @PutMapping
    ResponseEntity<ApiResponse<RoleResponse>> updateRole(@RequestParam Long id,
                 @RequestBody RoleUpdateRequest request){
        RoleResponse roleResponse = roleService.updateRole(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<RoleResponse>builder()
                        .message("Update role success")
                        .data(roleResponse)
                        .build()
        );
    }

    @Operation(summary = "Delete role", description = "Api for delete role by id")
    @DeleteMapping
    ResponseEntity<ApiResponse<?>> deleteRole(@RequestParam Long id){
        roleService.deleteRole(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .message("Delete role success!")
                        .build()
        );
    }
}
