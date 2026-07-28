package com.nhimex.assessment_collection.repository;

import com.nhimex.assessment_collection.entity.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DivisionRepository extends JpaRepository<Division, Long>, JpaSpecificationExecutor<Division> {

    Optional<Division> findByName(String name);

    boolean existsByName(String name);
}
