package com.employed.bar.infrastructure.dto.security;

import com.employed.bar.domain.enums.EmployeeRole;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema(description = "Solicitud para la creación de un usuario")

public class UserEntityRequest {
	
	private String firstname;
	
	private String lastname;
	
	private String email;
	
	private String password;

	@NotNull(message = "Role is mandatory")
	private EmployeeRole role;
	

}
