package com.employed.bar.infrastructure.security.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.employed.bar.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.UserEntityRepository;
import com.employed.bar.infrastructure.dto.security.request.LoginRequest;
import com.employed.bar.infrastructure.dto.security.response.TokenResponse;
import com.employed.bar.infrastructure.security.jwt.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	
	private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
	
	private final UserEntityRepository userEntityRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	@Override
	public TokenResponse login(LoginRequest loginRequest) {
		log.info("Intentando login para Usuario: {}", loginRequest.getEmail());
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						loginRequest.getEmail(),
						loginRequest.getPassword()
						)
				);
				
				UserEntity user = (UserEntity) authentication.getPrincipal();
				log.info("Login exitoso para usuario: {}", user.getEmail());
		
		
		return jwtService.generateToken(user.getEmail(), user.getRole().name());
	}

}
