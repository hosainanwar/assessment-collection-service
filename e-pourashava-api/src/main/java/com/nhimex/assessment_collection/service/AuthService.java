package com.nhimex.assessment_collection.service;

import com.nhimex.assessment_collection.dto.request_dto.LoginRequestDto;
import com.nhimex.assessment_collection.dto.response_dto.LoginResponseDto;
import com.nhimex.assessment_collection.security.CustomUserDetailsService;
import com.nhimex.assessment_collection.security.JwtTokenProvider;
import com.nhimex.assessment_collection.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;

    public LoginResponseDto login(LoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        if (!principal.matchesTenant(request.getTenantId())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return toResponse(principal);
    }

    public LoginResponseDto refresh(String refreshToken) {
        if (refreshToken == null || !tokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        UserPrincipal principal;
        try {
            principal = (UserPrincipal) userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException ex) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }
        if (!principal.isEnabled()) {
            throw new IllegalArgumentException("User is disabled");
        }

        return toResponse(principal);
    }

    public LoginResponseDto currentUser() {
        return toProfile(com.nhimex.assessment_collection.security.CurrentUserProvider.require());
    }

    private LoginResponseDto toResponse(UserPrincipal principal) {
        return toProfile(principal).toBuilder()
                .accessToken(tokenProvider.generateToken(principal))
                .refreshToken(tokenProvider.generateRefreshToken(principal.getUsername()))
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getJwtExpiration())
                .build();
    }

    private LoginResponseDto toProfile(UserPrincipal principal) {
        return LoginResponseDto.builder()
                .username(principal.getUsername())
                .tenantId(principal.getSubdomain())
                .pourashavaId(principal.getPourashavaId())
                .role(principal.getRoleCodes().isEmpty() ? null : principal.getRoleCodes().get(0))
                .roles(principal.getRoleCodes())
                .permissions(principal.getPermissionCodes())
                .subdomain(principal.getSubdomain())
                .build();
    }
}
