package com.nhimex.assessment_collection.repository;

import com.nhimex.assessment_collection.entity.Role;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByCode(String code);

    boolean existsByCode(String code);

    List<Role> findByStatusTrue();

    @EntityGraph(attributePaths = "permissions")
    List<Role> findByCodeIn(Collection<String> codes);

    @EntityGraph(attributePaths = "permissions")
    Optional<Role> findWithPermissionsById(Long id);

    @EntityGraph(attributePaths = "permissions")
    List<Role> findWithPermissionsByStatusTrue();
}
