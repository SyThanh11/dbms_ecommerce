package com.group12.ecommerce.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group12.ecommerce.entity.order.OrderEntity;
import com.group12.ecommerce.entity.role.RoleEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "user")
@Builder
@DynamicInsert
@DynamicUpdate
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(columnDefinition = "varchar(255) comment 'avatar'")
    String avatar;

    @Column(columnDefinition = "varchar(255) comment 'username'", nullable = false, unique = true)
    String username;

    @Column(columnDefinition = "varchar(255) comment 'password'", nullable = false)
    String password;

    @Column(columnDefinition = "varchar(255) comment 'email'", nullable = false)
    String email;

    @Column(columnDefinition = "varchar(255) comment 'full name'")
    String fullName;

    LocalDate dob;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    @JsonIgnore
    Set<RoleEntity> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    List<OrderEntity> orders;
}
