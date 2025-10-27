package com.employed.bar.infrastructure.adapter.in.controller.security;

import com.employed.bar.infrastructure.constants.ApiPathConstants;
import com.employed.bar.infrastructure.dto.security.request.CreateUserRequest;
import com.employed.bar.infrastructure.dto.security.request.LoginRequest;
import com.employed.bar.infrastructure.dto.security.response.TokenResponse;
import com.employed.bar.infrastructure.security.auth.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for user authentication and registration.
 * This controller handles login requests and the registration of new manager users,
 * acting as an inbound adapter to the application's authentication services.
 */
@RestController
@RequestMapping(ApiPathConstants.V1_ROUTE + ApiPathConstants.AUTH_ROUTE)
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register-manager")
    public ResponseEntity<Void> registerManager(@Valid @RequestBody CreateUserRequest request) {
        authService.registerManager(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
