package com.group12.ecommerce.base.commandlinerunner;

import com.group12.ecommerce.entity.role.RoleEntity;
import com.group12.ecommerce.repository.role.IRoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DataSeeder implements CommandLineRunner {
    IRoleRepository roleRepository;

    @Override
    public void run(String... args) {
        seedRoles();
    }

    private void seedRoles() {
        if (roleRepository.findByName("USER").isEmpty()) {
            RoleEntity userRole = RoleEntity.builder()
                    .name("USER")
                    .description("User role")
                    .build();
            roleRepository.save(userRole);
        }
    }
}
