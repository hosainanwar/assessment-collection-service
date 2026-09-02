package com.nhimex.assessment_collection.security;

import com.nhimex.assessment_collection.entity.Role;
import com.nhimex.assessment_collection.entity.User;
import com.nhimex.assessment_collection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PermissionResolver permissionResolver;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameWithTenant(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return toPrincipal(user);
    }

    public UserPrincipal toPrincipal(User user) {
        List<String> roleCodes = user.getRoles().stream()
                .filter(role -> Boolean.TRUE.equals(role.getStatus()))
                .map(Role::getCode)
                .collect(Collectors.toList());

        List<String> permissionCodes = permissionResolver.resolve(user.getId());

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        roleCodes.forEach(code -> authorities.add(new SimpleGrantedAuthority("ROLE_" + code)));
        permissionCodes.forEach(code -> authorities.add(new SimpleGrantedAuthority(code)));

        Long pourashavaId = user.getPourashava() != null ? user.getPourashava().getId() : null;
        String subdomain = user.getPourashava() != null
                ? user.getPourashava().getSubdomain()
                : user.getSubdomain();

        return UserPrincipal.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .pourashavaId(pourashavaId)
                .subdomain(subdomain)
                .roleCodes(roleCodes)
                .permissionCodes(permissionCodes)
                .enabled(user.getStatus() != null && user.getStatus())
                .authorities(authorities)
                .build();
    }
}
