package com.group12.ecommerce.dto.request.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String avatar;
    String username;
    String password;
    String email;
    String fullName;
    LocalDate dob;
    Set<Long> roleIds;
}
