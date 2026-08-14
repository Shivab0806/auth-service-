package com.auth.service.service.impl;

import com.auth.service.dto.AuthRequest;
import com.auth.service.dto.AuthResponse;
import com.auth.service.dto.RegisterRequest;
import com.auth.service.model.Roles;
import com.auth.service.model.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.auth.service.repo.UserRepository;
import com.auth.service.service.AuthService;
import com.auth.service.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final com.auth.service.service.RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository,
            com.auth.service.service.RoleService roleService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        Users user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        java.util.Set<String> roles = user.getRoles() == null ? java.util.Collections.emptySet()
                : user.getRoles().stream().map(Roles::getRole).collect(java.util.stream.Collectors.toSet());
        String token = jwtUtil.generateToken(user.getUsername(), roles);
        return new AuthResponse(token);
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // assign default ROLE_USER (expects ROLE_USER exists via Liquibase)
        Roles role = roleService.findByRole("ROLE_USER");
        java.util.Set<Roles> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        userRepository.save(user);
        return new AuthResponse("User Registered Successfully");
    }
}
