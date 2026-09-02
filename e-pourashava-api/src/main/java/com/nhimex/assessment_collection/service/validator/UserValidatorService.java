package com.nhimex.assessment_collection.service.validator;

import com.nhimex.assessment_collection.exception.UserInformException;
import com.nhimex.assessment_collection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidatorService {

    private final UserRepository userRepository;

    public void validateForCreate(String name, String username, String email, String password, String subdomain) {
        if (name == null || name.trim().isEmpty()) {
            throw new UserInformException("Name is required");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new UserInformException("Username is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new UserInformException("Email is required");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new UserInformException("Password is required");
        }
        if (userRepository.existsByUsername(username)) {
            throw new UserInformException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new UserInformException("Email already exists");
        }
    }

    public void validateForUpdate(Long id, String name, String username, String email, String subdomain) {
        if (id == null) {
            throw new UserInformException("User ID is required for update");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new UserInformException("Name is required");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new UserInformException("Username is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new UserInformException("Email is required");
        }
    }
}
