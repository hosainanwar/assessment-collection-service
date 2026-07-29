package com.nhimex.assessment_collection.repository;

import com.nhimex.assessment_collection.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    List<Word> findBySubdomain(String subdomain);

    List<Word> findByWordNameContainingIgnoreCase(String wordName);

    List<Word> findByWordNameContainingIgnoreCaseAndSubdomain(String wordName, String subdomain);
}
