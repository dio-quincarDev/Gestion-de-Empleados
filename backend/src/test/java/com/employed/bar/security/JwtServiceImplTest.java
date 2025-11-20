package com.employed.bar.security;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.infrastructure.dto.security.response.TokenResponse;
import com.employed.bar.infrastructure.security.jwt.JwtServiceImpl;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



import static org.junit.jupiter.api.Assertions.*;

class JwtServiceImplTest {

    private JwtServiceImpl jwtService;
    private static final String SECRET = "supersecretkeysupersecretkeysupersecretkey"; // 32+ chars

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl(SECRET);
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        String email = "test@example.com";
        String role = EmployeeRole.CASHIER.name();

        TokenResponse tokenResponse = jwtService.generateToken(email, role);

        assertNotNull(tokenResponse);
        assertNotNull(tokenResponse.getAccessToken());

        Claims claims = jwtService.getClaims(tokenResponse.getAccessToken());
        assertEquals(email, claims.getSubject());
        assertEquals(role, ((java.util.List<?>) claims.get("roles")).get(0));
    }

    @Test
    void getClaims_ShouldExtractClaimsFromValidToken() {
        String email = "test@example.com";
        String role = EmployeeRole.ADMIN.name();
        TokenResponse tokenResponse = jwtService.generateToken(email, role);

        Claims claims = jwtService.getClaims(tokenResponse.getAccessToken());

        assertNotNull(claims);
        assertEquals(email, claims.getSubject());
        assertEquals(role, ((java.util.List<?>) claims.get("roles")).get(0));
    }

    @Test
    void isExpired_ShouldReturnFalseForValidToken() {
        TokenResponse tokenResponse = jwtService.generateToken("test@example.com", EmployeeRole.CASHIER.name());

        boolean isExpired = jwtService.isExpired(tokenResponse.getAccessToken());

        assertFalse(isExpired);
    }

    @Test
    void isExpired_ShouldReturnTrueForExpiredToken() {
        // This is a simplified way to create an expired token for testing purposes.
        // In a real scenario, you might manipulate time using a library or custom clock.
        String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJleHBpcmVkQHRlc3QuY29tIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE2MTYxNjg0MDAsImV4cCI6MTYxNjE2ODQwMH0.5-mG_Gv_4qjZ3n3jX_pZ6Y8cZ_pZ6Y8cZ_pZ6Y8cZ_p";

        boolean isExpired = jwtService.isExpired(expiredToken);

        assertTrue(isExpired);
    }

    @Test
    void getClaims_ShouldThrowExceptionForInvalidToken() {
        String invalidToken = "this.is.not.a.valid.token";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            jwtService.getClaims(invalidToken);
        });

        assertTrue(exception.getMessage().contains("Token JWT inválido o expirado"));
    }

    @Test
    void extractEmail_ShouldReturnEmailFromValidToken() {
        String email = "user@domain.com";
        TokenResponse tokenResponse = jwtService.generateToken(email, EmployeeRole.MANAGER.name());

        String extractedEmail = jwtService.extractEmail(tokenResponse.getAccessToken());

        assertEquals(email, extractedEmail);
    }

    @Test
    void constructor_ShouldThrowExceptionForShortSecret() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new JwtServiceImpl("shortsecret");
        });
        assertEquals("La clave secreta JWT debe tener al menos 32 caracateres.", exception.getMessage());
    }
}
