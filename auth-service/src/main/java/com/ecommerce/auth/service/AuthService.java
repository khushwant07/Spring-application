package com.ecommerce.auth.service;

import com.ecommerce.auth.domain.Role;
import com.ecommerce.auth.domain.User;
import com.ecommerce.auth.repo.RoleRepository;
import com.ecommerce.auth.repo.UserRepository;
import com.ecommerce.auth.security.JwtService;
import com.ecommerce.auth.web.dto.LoginRequest;
import com.ecommerce.auth.web.dto.RegisterRequest;
import com.ecommerce.auth.web.dto.TokenResponse;
import com.ecommerce.auth.web.dto.UserProfileResponse;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final long jwtExpirationMs;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            @Value("${jwt.expiration-ms}") long jwtExpirationMs) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.jwtExpirationMs = jwtExpirationMs;
    }

    @Transactional
    public UserProfileResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalArgumentException("Email already registered");
        }
        Role userRole =
                roleRepository
                        .findByName("ROLE_USER")
                        .orElseThrow(() -> new IllegalStateException("ROLE_USER missing"));
        User user = new User();
        user.setEmail(request.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.getRoles().add(userRole);
        userRepository.save(user);
        return toProfile(user);
    }

    public TokenResponse login(LoginRequest request) {
        User user =
                userRepository
                        .findByEmailIgnoreCase(request.email().trim().toLowerCase())
                        .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!user.isEnabled()
                || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        Iterable<String> roles =
                user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        String token = jwtService.generateToken(user.getId(), user.getEmail(), roles);
        return new TokenResponse(token, "Bearer", jwtExpirationMs);
    }

    public UserProfileResponse me(com.ecommerce.auth.domain.User principal) {
        return toProfile(principal);
    }

    private UserProfileResponse toProfile(User user) {
        var roles = user.getRoles().stream().map(Role::getName).toList();
        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roles);
    }
}
