package com.nhimex.assessment_collection.service.validator;

import com.nhimex.assessment_collection.exception.UserInformException;
import org.springframework.stereotype.Service;

@Service
public class DivisionValidatorService {

    public void validateForCreate(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new UserInformException("Division name is required");
        }
    }

    public void validateForUpdate(Long id, String name) {
        if (id == null) {
            throw new UserInformException("Division ID is required for update");
        }
        validateForCreate(name);
    }
}
