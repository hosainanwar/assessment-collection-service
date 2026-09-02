package com.nhimex.assessment_collection.service.query;

import com.nhimex.assessment_collection.entity.User;
import com.nhimex.assessment_collection.exception.ResourceNotFoundException;
import com.nhimex.assessment_collection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAllWithRoles();
    }

    public User findById(Long id) {
        return userRepository.findWithRolesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public List<User> findBySubdomain(String subdomain) {
        return userRepository.findBySubdomain(subdomain);
    }

    public List<User> search(String name, String subdomain) {
        if (name != null && !name.isEmpty() && subdomain != null && !subdomain.isEmpty()) {
            return userRepository.findByNameContainingIgnoreCaseAndSubdomain(name, subdomain);
        } else if (name != null && !name.isEmpty()) {
            return userRepository.findByNameContainingIgnoreCase(name);
        } else if (subdomain != null && !subdomain.isEmpty()) {
            return userRepository.findBySubdomain(subdomain);
        }
        return userRepository.findAll();
    }
}
