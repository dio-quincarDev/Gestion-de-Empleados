package com.employed.bar.infrastructure.service;

import com.employed.bar.infrastructure.dto.security.LoginRequest;
import com.employed.bar.infrastructure.dto.security.TokenResponse;

public interface AuthService {
	
	TokenResponse login (LoginRequest loginRequest);

}
