package com.nhimex.assessment_collection.service.validator;

import com.nhimex.assessment_collection.exception.UserInformException;
import org.springframework.stereotype.Service;

@Service
public class PouroshovaInfoValidatorService {

    public void validateForCreate(String pouroshovaName, String meyorName, String psName,
                                   String dsName, String signatureName, String subdomain) {
        if (pouroshovaName == null || pouroshovaName.trim().isEmpty()) {
            throw new UserInformException("Pourashava name is required");
        }
        if (meyorName == null || meyorName.trim().isEmpty()) {
            throw new UserInformException("Mayor name is required");
        }
        if (psName == null || psName.trim().isEmpty()) {
            throw new UserInformException("Police station name is required");
        }
        if (dsName == null || dsName.trim().isEmpty()) {
            throw new UserInformException("District name is required");
        }
        if (signatureName == null || signatureName.trim().isEmpty()) {
            throw new UserInformException("Signature name is required");
        }
        if (subdomain == null || subdomain.trim().isEmpty()) {
            throw new UserInformException("Subdomain is required");
        }
    }

    public void validateForUpdate(Long id, String pouroshovaName, String meyorName, String psName,
                                   String dsName, String signatureName, String subdomain) {
        if (id == null) {
            throw new UserInformException("PouroshovaInfo ID is required for update");
        }
        validateForCreate(pouroshovaName, meyorName, psName, dsName, signatureName, subdomain);
    }
}
