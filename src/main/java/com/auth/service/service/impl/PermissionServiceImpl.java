package com.auth.service.service.impl;

import com.auth.service.model.Permission;
import com.auth.service.model.Roles;
import com.auth.service.repo.PermissionRepository;
import com.auth.service.repo.RoleRepository;
import com.auth.service.service.PermissionService;
import com.auth.service.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;

@Service
public class PermissionServiceImpl implements PermissionService {

    private static final Logger log = LoggerFactory.getLogger(PermissionServiceImpl.class);

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final RoleService roleService;

    public PermissionServiceImpl(PermissionRepository permissionRepository, RoleRepository roleRepository, RoleService roleService) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.roleService = roleService;
    }

    @Override
    public Permission findByPermission(String permission) {
        String normalized = normalizePermission(permission);
        return permissionRepository.findByPermission(normalized)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Permission not found"));
    }

    @Override
    @Transactional
    public void assignPermissionToRole(String roleName, String permissionName) {
        Roles role = roleService.findByRole(roleName);

        String normalized = normalizePermission(permissionName);
        Permission permission = permissionRepository.findByPermission(normalized)
                .orElseGet(() -> permissionRepository.save(new Permission(null, normalized)));

        java.util.Set<Permission> perms = role.getPermissions();
        if (perms == null) perms = new HashSet<>();
        if (perms.stream().anyMatch(p -> normalized.equals(p.getPermission()))) {
            log.info("Role '{}' already has permission '{}'", role.getRole(), normalized);
            return; // idempotent
        }
        perms.add(permission);
        role.setPermissions(perms);
        roleRepository.save(role);
        log.info("Assigned permission '{}' to role '{}'.", normalized, role.getRole());
    }

    private String normalizePermission(String permission) {
        if (permission == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "permission is required");
        permission = permission.trim();
        if (permission.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "permission is required");
        return permission.startsWith("PERM_") ? permission : "PERM_" + permission;
    }
}
