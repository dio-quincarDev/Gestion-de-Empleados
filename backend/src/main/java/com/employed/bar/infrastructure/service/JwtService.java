package com.employed.bar.infrastructure.service;

import com.employed.bar.infrastructure.dto.security.TokenResponse;

import io.jsonwebtoken.Claims;

public interface JwtService {
	
	TokenResponse generateToken(String email, String role);
	
	Claims getClaims(String token);
	
	boolean isExpired(String token);
	
	String extractEmail(String token);
	
	

}
