package com.group12.ecommerce.repository.token;

import com.group12.ecommerce.entity.token.InvalidatedTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IInvalidatedTokenRepository extends JpaRepository<InvalidatedTokenEntity, String>,
        JpaSpecificationExecutor<InvalidatedTokenEntity> {}
