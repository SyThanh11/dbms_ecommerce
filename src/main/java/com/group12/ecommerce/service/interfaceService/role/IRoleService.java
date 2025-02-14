package com.group12.ecommerce.service.interfaceService.role;

import com.group12.ecommerce.dto.request.role.RoleCreationRequest;
import com.group12.ecommerce.dto.request.role.RoleUpdateRequest;
import com.group12.ecommerce.dto.response.role.RoleResponse;

import java.util.List;

public interface IRoleService {
    RoleResponse createRole(RoleCreationRequest request);
    List<RoleResponse> getAllRoles();
    RoleResponse getRoleById(Long id);
    void deleteRole(Long id);
    RoleResponse updateRole(Long id, RoleUpdateRequest request);
}
