package com.group12.ecommerce.mapper.role;

import com.group12.ecommerce.dto.request.role.RoleCreationRequest;
import com.group12.ecommerce.dto.request.role.RoleUpdateRequest;
import com.group12.ecommerce.dto.response.role.RoleResponse;
import com.group12.ecommerce.entity.role.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IRoleMapper {
    @Mapping(target = "permissions", ignore = true)
    RoleEntity toRoleEntity(RoleCreationRequest request);
    RoleResponse toRoleResponse(RoleEntity roleEntity);
    List<RoleResponse> toListRoleResponse(List<RoleEntity> roleEntityList);
    @Mapping(target = "permissions", ignore = true)
    void updateRoleEntity(@MappingTarget RoleEntity role, RoleUpdateRequest request);
}
