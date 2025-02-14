package com.group12.ecommerce.entity.permission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group12.ecommerce.entity.role.RoleEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "permission")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(columnDefinition = "varchar(255) comment 'name'", nullable = false, unique = true)
    String name;

    @Column(columnDefinition = "varchar(255) comment 'description'")
    String description;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "permissions")
    @JsonIgnore
    private Set<RoleEntity> roles;
}
