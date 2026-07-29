package com.nhimex.assessment_collection.service.validator;

import com.nhimex.assessment_collection.exception.UserInformException;
import org.springframework.stereotype.Service;

@Service
public class WordValidatorService {

    public void validateForCreate(String wordName, String subdomain) {
        if (wordName == null || wordName.trim().isEmpty()) {
            throw new UserInformException("Word name is required");
        }
        if (subdomain == null || subdomain.trim().isEmpty()) {
            throw new UserInformException("Subdomain is required");
        }
    }

    public void validateForUpdate(Long id, String wordName, String subdomain) {
        if (id == null) {
            throw new UserInformException("Word ID is required for update");
        }
        validateForCreate(wordName, subdomain);
    }
}
