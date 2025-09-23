package com.employed.bar.infrastructure.security.jwt;

import com.employed.bar.infrastructure.dto.security.response.TokenResponse;

import io.jsonwebtoken.Claims;

public interface JwtService {
	
	TokenResponse generateToken(String email, String role);
	
	Claims getClaims(String token);
	
	boolean isExpired(String token);
	
	String extractEmail(String token);
	
	

}
