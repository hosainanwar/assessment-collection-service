package com.nhimex.assessment_collection.repository;

import com.nhimex.assessment_collection.entity.Pourashava;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PourashavaRepository extends JpaRepository<Pourashava, Long> {

    Optional<Pourashava> findBySubdomain(String subdomain);

    List<Pourashava> findByDistrictId(Long districtId);

    List<Pourashava> findByDivisionId(Long divisionId);

    boolean existsBySubdomain(String subdomain);

    boolean existsByEnNameAndDistrictId(String enName, Long districtId);

    List<Pourashava> findBySubdomainNotIgnoreCase(String subdomain);
}
