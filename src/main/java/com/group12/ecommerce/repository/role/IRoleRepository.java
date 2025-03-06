package com.group12.ecommerce.repository.role;

import com.group12.ecommerce.entity.role.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface IRoleRepository extends JpaRepository<RoleEntity, Long>,
        JpaSpecificationExecutor<RoleEntity> {
    Optional<RoleEntity> findByName(String name);
}
