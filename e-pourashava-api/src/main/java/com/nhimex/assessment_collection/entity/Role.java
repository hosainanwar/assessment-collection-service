package com.nhimex.assessment_collection.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "roles")
public class Role extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "name_bn", nullable = false, length = 100)
    private String nameBn;

    @Column(name = "name_en", nullable = false, length = 100)
    private String nameEn;

    @Column(name = "description")
    private String description;

    /**
     * Seeded roles the API refuses to rename or delete.
     */
    @Column(name = "is_system", nullable = false)
    @Builder.Default
    private Boolean isSystem = false;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private Boolean status = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @Builder.Default
    private Set<Permission> permissions = new LinkedHashSet<>();

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Role role)) {
            return false;
        }
        return code != null && code.equals(role.getCode());
    }

    @Override
    public int hashCode() {
        return code == null ? 0 : code.hashCode();
    }
}
