package com.group12.ecommerce.service.interfaceService.user;

import com.group12.ecommerce.dto.request.user.UserUpdateRequest;
import com.group12.ecommerce.dto.response.page.CustomPageResponse;
import com.group12.ecommerce.dto.response.user.UserResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(String id);
    UserResponse updateUser(String id, UserUpdateRequest request);
    void deleteUser(String id);

    // pageable
    CustomPageResponse<UserResponse> getAllUsersWithPage(Pageable pageable);
}
