package com.group12.ecommerce.dto.response.user;

import com.group12.ecommerce.entity.role.RoleEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String avatar;
    String username;
    String email;
    String fullName;
    LocalDate dob;
    Set<RoleEntity> roles;
}
