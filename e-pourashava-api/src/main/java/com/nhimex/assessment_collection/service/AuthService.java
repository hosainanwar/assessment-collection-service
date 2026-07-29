package com.nhimex.assessment_collection.service;

import com.nhimex.assessment_collection.dto.request_dto.LoginRequestDto;
import com.nhimex.assessment_collection.dto.response_dto.LoginResponseDto;
import com.nhimex.assessment_collection.entity.User;
import com.nhimex.assessment_collection.repository.UserRepository;
import com.nhimex.assessment_collection.security.JwtTokenProvider;
import com.nhimex.assessment_collection.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    public LoginResponseDto login(LoginRequestDto request) {
        TenantContext.setCurrentTenant(request.getTenantId());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .username(user.getUsername())
                .tenantId(request.getTenantId())
                .role(user.getRole())
                .build();
    }

    public LoginResponseDto refresh(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = tokenProvider.generateToken(username);
        String newRefreshToken = tokenProvider.generateRefreshToken(username);

        return LoginResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}
