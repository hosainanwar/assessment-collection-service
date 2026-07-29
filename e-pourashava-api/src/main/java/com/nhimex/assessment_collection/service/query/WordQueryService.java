package com.nhimex.assessment_collection.service.query;

import com.nhimex.assessment_collection.entity.Word;
import com.nhimex.assessment_collection.exception.ResourceNotFoundException;
import com.nhimex.assessment_collection.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WordQueryService {

    private final WordRepository wordRepository;

    public List<Word> findAll() {
        return wordRepository.findAll();
    }

    public Word findById(Long id) {
        return wordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Word", "id", id));
    }

    public List<Word> findBySubdomain(String subdomain) {
        return wordRepository.findBySubdomain(subdomain);
    }

    public List<Word> search(String wordName, String subdomain) {
        if (wordName != null && !wordName.isEmpty() && subdomain != null && !subdomain.isEmpty()) {
            return wordRepository.findByWordNameContainingIgnoreCaseAndSubdomain(wordName, subdomain);
        } else if (wordName != null && !wordName.isEmpty()) {
            return wordRepository.findByWordNameContainingIgnoreCase(wordName);
        } else if (subdomain != null && !subdomain.isEmpty()) {
            return wordRepository.findBySubdomain(subdomain);
        }
        return wordRepository.findAll();
    }
}
