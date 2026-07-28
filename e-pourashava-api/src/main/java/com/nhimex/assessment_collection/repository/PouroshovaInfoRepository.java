package com.nhimex.assessment_collection.repository;

import com.nhimex.assessment_collection.entity.PouroshovaInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PouroshovaInfoRepository extends JpaRepository<PouroshovaInfo, Long> {

    Optional<PouroshovaInfo> findBySubdomain(String subdomain);

    boolean existsBySubdomain(String subdomain);
}
