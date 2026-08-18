package com.auth.service.service.impl;

import com.auth.service.model.Roles;
import com.auth.service.model.Users;
import com.auth.service.repo.RoleRepository;
import com.auth.service.repo.UserRepository;
import com.auth.service.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.HashSet;

@Service
public class RoleServiceImpl implements RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public RoleServiceImpl(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Roles findByRole(String role) {
        String normalized = normalizeRole(role);
        return roleRepository.findByRole(normalized)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));
    }

    @Override
    @Transactional
    public void assignRoleToUser(String username, String roleName) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        String normalized = normalizeRole(roleName);
        Roles role = roleRepository.findByRole(normalized)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        java.util.Set<Roles> roles = user.getRoles();
        if (roles == null)
            roles = new HashSet<>();
        if (roles.stream().anyMatch(r -> normalized.equals(r.getRole()))) {
            log.info("User '{}' already has role '{}'", username, normalized);
            return; // idempotent
        }
        roles.add(role);
        user.setRoles(roles);
        userRepository.save(user);
        log.info("Assigned role '{}' to user '{}'", normalized, username);
    }

    private String normalizeRole(String role) {
        if (role == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "role is required");
        role = role.trim();
        if (role.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "role is required");
        return role.startsWith("ROLE_") ? role : "ROLE_" + role;
    }
}
