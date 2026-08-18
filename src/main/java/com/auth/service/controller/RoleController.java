package com.auth.service.controller;

import com.auth.service.dto.AssignRoleRequest;
import com.auth.service.dto.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.auth.service.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;

@RestController
@RequestMapping("/api/admin")
public class RoleController {

    private static final Logger log = LoggerFactory.getLogger(RoleController.class);

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/assign-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> assignRole(@RequestBody @Valid AssignRoleRequest request) {
        log.debug("Assign role request: username='{}', role='{}'", request.getUsername(), request.getRole());
        roleService.assignRoleToUser(request.getUsername(), request.getRole());
        return ResponseEntity.ok(new MessageResponse("Role assigned to user"));
    }

}
