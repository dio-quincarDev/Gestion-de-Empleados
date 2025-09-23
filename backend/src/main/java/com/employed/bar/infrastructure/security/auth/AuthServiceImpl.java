package com.employed.bar.infrastructure.security.auth;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.exceptions.EmailAlreadyExistException;
import com.employed.bar.infrastructure.dto.security.request.CreateUserRequest;
import org.springframework.security.access.AccessDeniedException;

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

		if (user.getRole() != EmployeeRole.ADMIN && user.getRole() != EmployeeRole.MANAGER) {
			log.warn("Intento de login de usuario no autorizado: {} con rol: {}", user.getEmail(), user.getRole());
			throw new AccessDeniedException("El usuario no tiene privilegios para acceder al sistema.");
		}

		log.info("Login exitoso para usuario: {}", user.getEmail());
		
		
		return jwtService.generateToken(user.getEmail(), user.getRole().name());
	}

	@Override
	public void registerManager(CreateUserRequest request) {
		if (request.getRole() != EmployeeRole.MANAGER) {
			throw new IllegalArgumentException("This endpoint is only for registering the MANAGER.");
		}

		if (userEntityRepository.existsByRole(EmployeeRole.MANAGER)) {
			throw new IllegalStateException("A MANAGER account already exists.");
		}

		if (userEntityRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new EmailAlreadyExistException("Email already exists: " + request.getEmail());
		}

		UserEntity user = UserEntity.builder()
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.firstname(request.getFirstname())
				.lastname(request.getLastname())
				.role(request.getRole())
				.build();

		userEntityRepository.save(user);
	}

}
