package com.employed.bar.infrastructure.dto.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema(description = "Respuesta que contiene el token de acceso JWT")
public class TokenResponse {
	
	@Schema(description = "Token de acceso JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
	private String accessToken;

}
