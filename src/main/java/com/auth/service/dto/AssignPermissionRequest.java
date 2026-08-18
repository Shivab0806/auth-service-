package com.auth.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AssignPermissionRequest {
    @NotBlank
    private String role;

    @NotBlank
    private String permission;
}
