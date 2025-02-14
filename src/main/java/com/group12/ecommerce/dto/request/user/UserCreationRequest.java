package com.group12.ecommerce.dto.request.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    String avatar;
    String username;
    String password;
    String email;
    String fullName;
    LocalDate dob;
}
