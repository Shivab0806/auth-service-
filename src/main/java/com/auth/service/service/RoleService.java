package com.auth.service.service;

import com.auth.service.model.Roles;

public interface RoleService {
    Roles findByRole(String role);

    void assignRoleToUser(String username, String roleName);
}
