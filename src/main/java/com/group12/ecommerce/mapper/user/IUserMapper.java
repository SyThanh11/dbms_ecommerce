package com.group12.ecommerce.mapper.user;

import com.group12.ecommerce.dto.request.role.RoleUpdateRequest;
import com.group12.ecommerce.dto.request.user.UserCreationRequest;
import com.group12.ecommerce.dto.request.user.UserUpdateRequest;
import com.group12.ecommerce.dto.response.user.UserResponse;
import com.group12.ecommerce.entity.role.RoleEntity;
import com.group12.ecommerce.entity.user.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IUserMapper {
    UserEntity toUserEntity(UserCreationRequest request);
    UserResponse toUserResponse(UserEntity userEntity);
    List<UserResponse> toListUserResponse(List<UserEntity> userEntityList);
    @Mapping(target = "roles", ignore = true)
    void updateUserEntity(@MappingTarget UserEntity user, UserUpdateRequest request);
}
