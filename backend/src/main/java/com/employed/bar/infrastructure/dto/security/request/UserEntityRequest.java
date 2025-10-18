package com.employed.bar.infrastructure.dto.security.request;

import com.employed.bar.domain.enums.EmployeeRole;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
	
	@NotBlank(message = "First name is mandatory")
	private String firstname;
	
	@NotBlank(message = "Last name is mandatory")
	private String lastname;
	
	@Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
	private String email;
	
	@NotBlank(message = "Password is mandatory")
	private String password;

	@NotNull(message = "Role is mandatory")
	private EmployeeRole role;
	

}
