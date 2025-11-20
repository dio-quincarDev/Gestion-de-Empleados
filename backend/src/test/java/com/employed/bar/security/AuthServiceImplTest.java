package com.employed.bar.security;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.employed.bar.infrastructure.dto.security.request.LoginRequest;
import com.employed.bar.infrastructure.dto.security.response.TokenResponse;
import com.employed.bar.infrastructure.security.auth.AuthServiceImpl;
import com.employed.bar.infrastructure.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void login_Successful() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("user@test.com", "password");
        UserEntity userEntity = new UserEntity(UUID.randomUUID(), "Test User", "user@test.com", "encodedPassword",
                EmployeeRole.ADMIN);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userEntity, null, userEntity.getAuthorities());
        TokenResponse expectedTokenResponse = TokenResponse.builder().accessToken("test-token").build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtService.generateToken(userEntity.getEmail(), "ROLE_" + userEntity.getRole().name())).thenReturn(expectedTokenResponse);

        // Act
        TokenResponse actualTokenResponse = authService.login(loginRequest);

        // Assert
        assertNotNull(actualTokenResponse);
        assertEquals(expectedTokenResponse.getAccessToken(), actualTokenResponse.getAccessToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(userEntity.getEmail(), "ROLE_" + userEntity.getRole().name());
    }

    @Test
    void login_Failed_BadCredentials() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("user@test.com", "wrong-password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginRequest);
        });

        verify(jwtService, never()).generateToken(anyString(), anyString());
    }
}
