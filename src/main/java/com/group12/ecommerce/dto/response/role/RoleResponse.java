package com.group12.ecommerce.dto.response.role;

import com.group12.ecommerce.entity.permission.PermissionEntity;
import com.group12.ecommerce.entity.user.UserEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponse {
    Long id;
    String name;
    String description;
    Set<PermissionEntity> permissions;
    Set<UserEntity> users;
}
