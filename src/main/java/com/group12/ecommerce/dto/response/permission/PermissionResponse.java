package com.group12.ecommerce.dto.response.permission;

import com.group12.ecommerce.entity.role.RoleEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionResponse {
    Long id;
    String name;
    String description;
    Set<RoleEntity> roles;
}
