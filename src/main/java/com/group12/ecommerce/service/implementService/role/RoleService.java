package com.group12.ecommerce.service.implementService.role;

import com.group12.ecommerce.base.exception.AppException;
import com.group12.ecommerce.base.exception.ErrorCode;
import com.group12.ecommerce.dto.request.role.RoleCreationRequest;
import com.group12.ecommerce.dto.request.role.RoleUpdateRequest;
import com.group12.ecommerce.dto.response.role.RoleResponse;
import com.group12.ecommerce.entity.permission.PermissionEntity;
import com.group12.ecommerce.entity.role.RoleEntity;
import com.group12.ecommerce.mapper.role.IRoleMapper;
import com.group12.ecommerce.repository.permission.IPermissionRepository;
import com.group12.ecommerce.repository.role.IRoleRepository;
import com.group12.ecommerce.service.interfaceService.role.IRoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService implements IRoleService {
    @Autowired
    IRoleRepository roleRepository;
    @Autowired
    IPermissionRepository permissionRepository;
    @Autowired
    IRoleMapper roleMapper;

    @Override
    public RoleResponse createRole(RoleCreationRequest request) {
        try{
            RoleEntity roleEntity = roleMapper.toRoleEntity(request);

            List<PermissionEntity> permissionEntityList = permissionRepository
                    .findAllById(request.getPermissionIds());

            roleEntity.setPermissions(new HashSet<>(permissionEntityList));

            return roleMapper.toRoleResponse(roleRepository.save(roleEntity));
        } catch (DataIntegrityViolationException e){
            throw new AppException(ErrorCode.NAME_ROLE_EXISTED);
        }
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        return roleMapper.toListRoleResponse(roleRepository.findAll());
    }

    @Override
    public RoleResponse getRoleById(Long id) {
        RoleEntity role = roleRepository.findById(id)
                .orElseThrow(() ->
                    new AppException(ErrorCode.NAME_ROLE_NOT_EXISTED)
                );

        return roleMapper.toRoleResponse(role);
    }


    @Override
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    @Override
    public RoleResponse updateRole(Long id, RoleUpdateRequest request) {
        RoleEntity role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        roleMapper.updateRoleEntity(role, request);
        List<PermissionEntity> permissionEntityList = permissionRepository
                .findAllById(request.getPermissionIds());
        role.setPermissions(new HashSet<>(permissionEntityList));

        return roleMapper.toRoleResponse(roleRepository.save(role));
    }
}
