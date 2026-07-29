package com.nhimex.assessment_collection.repository;

import com.nhimex.assessment_collection.entity.Para;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParaRepository extends JpaRepository<Para, Long> {

    List<Para> findByWordId(Long wordId);

    List<Para> findBySubdomain(String subdomain);

    List<Para> findByWordIdAndSubdomain(Long wordId, String subdomain);
}
