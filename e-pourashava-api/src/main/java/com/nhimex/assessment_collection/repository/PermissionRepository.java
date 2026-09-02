package com.nhimex.assessment_collection.repository;

import com.nhimex.assessment_collection.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByCode(String code);

    List<Permission> findByCodeIn(Collection<String> codes);

    List<Permission> findAllByOrderByModuleAscActionAsc();

    @Query("""
            SELECT DISTINCT p.code
            FROM User u
            JOIN u.roles r
            JOIN r.permissions p
            WHERE u.id = :userId
              AND r.status = true
            """)
    List<String> findCodesByUserId(@Param("userId") Long userId);
}
