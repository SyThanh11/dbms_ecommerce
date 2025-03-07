package com.group12.ecommerce.service.implementService.user;

import com.group12.ecommerce.base.exception.AppException;
import com.group12.ecommerce.base.exception.ErrorCode;
import com.group12.ecommerce.dto.request.user.UserUpdateRequest;
import com.group12.ecommerce.dto.response.page.CustomPageResponse;
import com.group12.ecommerce.dto.response.user.UserResponse;
import com.group12.ecommerce.entity.role.RoleEntity;
import com.group12.ecommerce.entity.user.UserEntity;
import com.group12.ecommerce.mapper.user.IUserMapper;
import com.group12.ecommerce.repository.role.IRoleRepository;
import com.group12.ecommerce.repository.user.IUserRepository;
import com.group12.ecommerce.service.interfaceService.user.IUserService;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements IUserService {
    @Autowired
    IUserRepository userRepository;
    @Autowired
    IRoleRepository roleRepository;

    @Autowired
    IUserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public List<UserResponse> getAllUsers() {
        return userMapper.toListUserResponse(userRepository.findAll());
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public UserResponse getUserById(String id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public UserResponse updateUser(String id, UserUpdateRequest request) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        userMapper.updateUserEntity(user, request);

        List<RoleEntity> roleEntityList = roleRepository.findAllById(request.getRoleIds());
        user.setRoles(new HashSet<>(roleEntityList));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public CustomPageResponse<UserResponse> getAllUsersWithPage(Pageable pageable) {
        Page<UserEntity> userEntities = userRepository.findAll(pageable);
        Page<UserResponse> userResponses = userEntities.map(userMapper::toUserResponse);
        return CustomPageResponse.fromPage(userResponses);
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public CustomPageResponse<UserResponse> searchUsers(String username, String email, String fullName, Pageable pageable) {
        Specification<UserEntity> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (username != null && !username.isBlank()) {
                predicates.add(criteriaBuilder.like(root.get("username"), "%" + username + "%"));
            }
            if (email != null && !email.isBlank()) {
                predicates.add(criteriaBuilder.like(root.get("email"), "%" + email + "%"));
            }
            if (fullName != null && !fullName.isBlank()) {
                predicates.add(criteriaBuilder.like(root.get("fullName"), "%" + fullName + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<UserEntity> userPage = userRepository.findAll(spec, pageable);
        Page<UserResponse> userResponses = userPage.map(userMapper::toUserResponse);
        return CustomPageResponse.fromPage(userResponses);
    }

}
