package com.employed.bar.infrastructure.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.employed.bar.infrastructure.dto.security.TokenResponse;
import com.employed.bar.infrastructure.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtService {
	
	private static final Logger log = LoggerFactory.getLogger(JwtServiceImpl.class);
	private final SecretKey secretKey;
	private static final long EXPIRATION_TIME = 864_000_000;
	
	public JwtServiceImpl(@Value("${jwt.secret}") String secret) {
		if (secret.getBytes().length < 32) {
			throw new IllegalArgumentException("La clave secreta JWT debe tener al menos 32 caracateres.");
		}
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}
	

	@Override
	public TokenResponse generateToken(String email, String role) {
		Date now = new Date();
		Date expirationDate = new Date(now.getTime() + EXPIRATION_TIME);
		
		String token = Jwts.builder()
				.subject(email)
				.claim("role", role)
				.issuedAt(now)
				.expiration(expirationDate)
				.signWith(secretKey)
				.compact();
		
		return TokenResponse.builder()
				.accessToken(token)
				.build();
	}

	@Override
	public Claims getClaims(String token) {
		try {
			return Jwts.parser()
					.verifyWith(secretKey)
					.build()
					.parseSignedClaims(token)
					.getPayload();
		}catch (Exception e) {
		 log.error("Error al parsear JWT: {}, Causa: {}", e.getMessage(), e.getCause() != null ? e.getCause().getMessage() : "N/A");
	     throw new IllegalArgumentException("Token JWT inválido o expirado", e);
			
		}
		
	}

	@Override
	public boolean isExpired(String token) {
		try {
			return getClaims(token).getExpiration().before(new Date());
		} catch (Exception e) {
			
			return true;
		}
		
	}

	@Override
	public String extractEmail(String token) {
		
		return getClaims(token).getSubject();
	}

}
