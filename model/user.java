package com.hashedin.huspark.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hashedin.huspark.util.MaskingSerializer;
import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "users_servicenest")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @JsonSerialize(using = MaskingSerializer.class)  // 👈 Auto mask email
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;

    @JsonSerialize(using = MaskingSerializer.class)  // 👈 Auto mask phone
    private String phone;

    private String address;

    private boolean active = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles_map",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
}
