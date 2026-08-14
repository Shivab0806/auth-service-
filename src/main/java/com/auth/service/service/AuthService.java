package com.auth.service.service;

import com.auth.service.dto.AuthRequest;
import com.auth.service.dto.AuthResponse;
import com.auth.service.dto.RegisterRequest;

public interface AuthService {
    AuthResponse login(AuthRequest request);
    AuthResponse register(RegisterRequest request);
}
