package com.nhimex.assessment_collection.service.validator;

import com.nhimex.assessment_collection.exception.UserInformException;
import org.springframework.stereotype.Service;

@Service
public class ParaValidatorService {

    public void validateForCreate(String pbrName, Long wordId, String subdomain) {
        if (pbrName == null || pbrName.trim().isEmpty()) {
            throw new UserInformException("Para name is required");
        }
        if (wordId == null) {
            throw new UserInformException("Word ID is required");
        }
        if (subdomain == null || subdomain.trim().isEmpty()) {
            throw new UserInformException("Subdomain is required");
        }
    }

    public void validateForUpdate(Long id, String pbrName, Long wordId, String subdomain) {
        if (id == null) {
            throw new UserInformException("Para ID is required for update");
        }
        validateForCreate(pbrName, wordId, subdomain);
    }
}
