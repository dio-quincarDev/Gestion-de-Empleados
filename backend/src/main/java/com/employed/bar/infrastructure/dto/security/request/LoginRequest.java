package com.employed.bar.infrastructure.dto.security.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Schema(description = "Solicitud de Autenticación de usuario")
public class LoginRequest {

	@Email(message = "El formato del email no es válido")
	@NotBlank(message = "El email no puede estar vacío")
    @Schema(description = "Correo electrónico del usuario", example = "admin@example.com")
	private String email;
	
	@NotBlank(message = "La contraseña no puede estar vacía")
	@Schema(description = "Contraseña del usuario", example = "password123")
	private String password;

}
