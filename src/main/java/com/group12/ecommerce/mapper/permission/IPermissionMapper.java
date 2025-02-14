package com.group12.ecommerce.mapper.permission;


import com.group12.ecommerce.dto.request.permission.PermissionCreationRequest;
import com.group12.ecommerce.dto.request.permission.PermissionUpdateRequest;
import com.group12.ecommerce.dto.response.permission.PermissionResponse;
import com.group12.ecommerce.entity.permission.PermissionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IPermissionMapper {
    PermissionEntity toPermissionEntity(PermissionCreationRequest request);
    PermissionResponse toPermissionResponse(PermissionEntity permissionEntity);
    List<PermissionResponse> toListPermissionResponse(List<PermissionEntity> permissionEntityList);
    void updatePermissionEntity(@MappingTarget PermissionEntity permission, PermissionUpdateRequest request);
}
