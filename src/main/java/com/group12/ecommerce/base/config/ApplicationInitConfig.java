package com.group12.ecommerce.base.config;

import com.group12.ecommerce.entity.permission.PermissionEntity;
import com.group12.ecommerce.entity.role.RoleEntity;
import com.group12.ecommerce.entity.user.UserEntity;
import com.group12.ecommerce.repository.permission.IPermissionRepository;
import com.group12.ecommerce.repository.role.IRoleRepository;
import com.group12.ecommerce.repository.user.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashSet;

@Slf4j
@Configuration
public class ApplicationInitConfig {

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driver-class-name",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(
            IUserRepository userRepository, IRoleRepository roleRepository, IPermissionRepository permissionRepository,
            PasswordEncoder passwordEncoder, @Value("${security.user.password}") String password
            ) {
        return args -> {
            PermissionEntity manageUsersPermission = permissionRepository.findByName("MANAGE_USERS")
                    .orElseGet(() -> {
                        PermissionEntity newPermissionEntity = PermissionEntity.builder()
                                .name("MANAGE_USERS")
                                .description("Allow managing users")
                                .build();
                        return permissionRepository.save(newPermissionEntity);
                    });

            RoleEntity adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> {
                        RoleEntity newRole = RoleEntity.builder()
                                .name("ADMIN")
                                .description("Administrator role")
                                .build();
                        return roleRepository.save(newRole);
                    });

            adminRole.setPermissions(new HashSet<>(Collections.singletonList(manageUsersPermission)));
            roleRepository.save(adminRole);

            if (userRepository.findByUsername("admin").isEmpty()) {
                UserEntity user = UserEntity.builder()
                        .username("admin")
                        .password(passwordEncoder.encode(password))
                        .email("sythanhdev@gmail.com")
                        .build();
                userRepository.save(user);

                user.setRoles(new HashSet<>(Collections.singletonList(adminRole)));
                userRepository.save(user);
            }
        };
    }
}
