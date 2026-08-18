package com.auth.service.service;

import com.auth.service.model.Permission;

public interface PermissionService {
    Permission findByPermission(String permission);

    void assignPermissionToRole(String roleName, String permissionName);
}
