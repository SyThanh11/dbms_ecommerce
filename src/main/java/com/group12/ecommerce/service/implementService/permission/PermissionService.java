package com.group12.ecommerce.service.implementService.permission;

import com.group12.ecommerce.base.exception.AppException;
import com.group12.ecommerce.base.exception.ErrorCode;
import com.group12.ecommerce.dto.request.permission.PermissionCreationRequest;
import com.group12.ecommerce.dto.request.permission.PermissionUpdateRequest;
import com.group12.ecommerce.dto.response.permission.PermissionResponse;
import com.group12.ecommerce.entity.permission.PermissionEntity;
import com.group12.ecommerce.mapper.permission.IPermissionMapper;
import com.group12.ecommerce.repository.permission.IPermissionRepository;
import com.group12.ecommerce.service.interfaceService.permission.IPermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService implements IPermissionService {
    @Autowired
    IPermissionRepository permissionRepository;
    @Autowired
    IPermissionMapper permissionMapper;

    @Override
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public PermissionResponse createPermission(PermissionCreationRequest request) {
        try {
            PermissionEntity permission = permissionMapper.toPermissionEntity(request);

            return permissionMapper.toPermissionResponse(
                    permissionRepository.save(permission)
            );
        } catch(DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.NAME_PERMISSION_EXISTED);
        }
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public List<PermissionResponse> getAllPermissions() {
        return permissionMapper.toListPermissionResponse(
                permissionRepository.findAll()
        );
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public PermissionResponse getPermissionById(Long id) {
        PermissionEntity permissionEntity = permissionRepository.findById(id)
                .orElseThrow(()
                -> new AppException(ErrorCode.NAME_PERMISSION_NOT_EXISTED));
        return permissionMapper.toPermissionResponse(permissionEntity);
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public void deletePermission(Long id) {
        permissionRepository.deleteById(id);
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public PermissionResponse updatePermission(Long id, PermissionUpdateRequest request) {
        PermissionEntity currentPermission = permissionRepository.findById(id)
                .orElseThrow(()
                        -> new AppException(ErrorCode.NAME_PERMISSION_NOT_EXISTED));

        permissionMapper.updatePermissionEntity(currentPermission, request);

        return permissionMapper.toPermissionResponse(
                permissionRepository.save(currentPermission)
        );
    }
}
