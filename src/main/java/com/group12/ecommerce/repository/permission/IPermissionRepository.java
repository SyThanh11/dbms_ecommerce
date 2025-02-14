package com.group12.ecommerce.repository.permission;

import com.group12.ecommerce.entity.permission.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IPermissionRepository extends JpaRepository<PermissionEntity, Long>,
        JpaSpecificationExecutor<PermissionEntity> {}
