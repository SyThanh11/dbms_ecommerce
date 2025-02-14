package com.group12.ecommerce.controller.permission;

import com.group12.ecommerce.dto.request.permission.PermissionCreationRequest;
import com.group12.ecommerce.dto.request.permission.PermissionUpdateRequest;
import com.group12.ecommerce.dto.response.api.ApiResponse;
import com.group12.ecommerce.dto.response.permission.PermissionResponse;
import com.group12.ecommerce.service.interfaceService.permission.IPermissionService;
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
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Permission Management")
public class PermissionController {
    @Autowired
    IPermissionService permissionService;

    @PostMapping
    ResponseEntity<ApiResponse<PermissionResponse>> createPermission(@RequestBody PermissionCreationRequest request){
        PermissionResponse permissionResponse = permissionService.createPermission(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
          ApiResponse.<PermissionResponse>builder()
                  .message("Create permission success!")
                  .data(permissionResponse)
                  .build()
        );
    }

    @GetMapping
    ResponseEntity<ApiResponse<List<PermissionResponse>>> getAllPermissions(){
        List<PermissionResponse> responseList = permissionService.getAllPermissions();

        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse.<List<PermissionResponse>>builder()
                    .message("Get all permissions success!")
                    .data(responseList)
                    .build()
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<PermissionResponse>> getPermissionById(@PathVariable Long id){
        PermissionResponse permissionResponse = permissionService.getPermissionById(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<PermissionResponse>builder()
                        .message("Get permission success!")
                        .data(permissionResponse)
                        .build()
        );
    }

    @PutMapping
    ResponseEntity<ApiResponse<PermissionResponse>> updatePermission(
            @RequestParam Long id, @RequestBody PermissionUpdateRequest permissionUpdateRequest){
        PermissionResponse permissionResponse = permissionService.updatePermission(id, permissionUpdateRequest);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<PermissionResponse>builder()
                        .message("Update permission success!")
                        .data(permissionResponse)
                        .build()
        );
    }

    @DeleteMapping
    ResponseEntity<ApiResponse> deletePermission(@RequestParam Long id){
        permissionService.deletePermission(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .message("Delete success!")
                        .build()
        );
    }
}
