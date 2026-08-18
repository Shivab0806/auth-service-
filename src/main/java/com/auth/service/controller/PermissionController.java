package com.auth.service.controller;

import com.auth.service.dto.AssignPermissionRequest;
import com.auth.service.dto.MessageResponse;
import com.auth.service.service.PermissionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class PermissionController {

    private static final Logger log = LoggerFactory.getLogger(PermissionController.class);

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/assign-permission")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> assignPermission(@RequestBody @Valid AssignPermissionRequest request) {
        log.debug("Assign permission request: role='{}', permission='{}'", request.getRole(), request.getPermission());
        permissionService.assignPermissionToRole(request.getRole(), request.getPermission());
        return ResponseEntity.ok(new MessageResponse("Permission assigned to role"));
    }
}
