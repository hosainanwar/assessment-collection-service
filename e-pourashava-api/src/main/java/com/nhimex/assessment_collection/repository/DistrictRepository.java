package com.nhimex.assessment_collection.repository;

import com.nhimex.assessment_collection.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long>, JpaSpecificationExecutor<District> {

    List<District> findByDivisionId(Long divisionId);

    Optional<District> findByName(String name);

    Optional<District> findByEnName(String enName);

    boolean existsByNameAndDivisionId(String name, Long divisionId);
}
