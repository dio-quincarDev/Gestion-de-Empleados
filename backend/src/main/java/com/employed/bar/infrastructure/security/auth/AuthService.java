package com.employed.bar.infrastructure.security.auth;

import com.employed.bar.infrastructure.dto.security.request.CreateUserRequest;
import com.employed.bar.infrastructure.dto.security.request.LoginRequest;
import com.employed.bar.infrastructure.dto.security.response.TokenResponse;

public interface AuthService {
	TokenResponse login(LoginRequest loginRequest);
	void registerManager(CreateUserRequest request);
