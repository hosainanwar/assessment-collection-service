package com.nhimex.assessment_collection.repository;

import com.nhimex.assessment_collection.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query("SELECT DISTINCT u FROM User u JOIN FETCH u.pourashava LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithTenant(@Param("username") String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findBySubdomain(String subdomain);

    List<User> findByNameContainingIgnoreCase(String name);

    List<User> findByNameContainingIgnoreCaseAndSubdomain(String name, String subdomain);

    long countByRoles_Id(Long roleId);

    @EntityGraph(attributePaths = {"roles", "pourashava"})
    @Query("SELECT DISTINCT u FROM User u")
    List<User> findAllWithRoles();

    @EntityGraph(attributePaths = {"roles", "pourashava"})
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findWithRolesById(@Param("id") Long id);
}
