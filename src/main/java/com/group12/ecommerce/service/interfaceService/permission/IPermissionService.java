package com.group12.ecommerce.service.interfaceService.permission;

import com.group12.ecommerce.dto.request.permission.PermissionCreationRequest;
import com.group12.ecommerce.dto.request.permission.PermissionUpdateRequest;
import com.group12.ecommerce.dto.response.permission.PermissionResponse;

import java.util.List;

public interface IPermissionService {
    PermissionResponse createPermission(PermissionCreationRequest request);
    List<PermissionResponse> getAllPermissions();
    PermissionResponse getPermissionById(Long id);
    void deletePermission(Long id);
    PermissionResponse updatePermission(Long id, PermissionUpdateRequest request);
}
