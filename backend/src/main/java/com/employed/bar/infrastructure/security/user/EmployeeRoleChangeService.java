package com.employed.bar.infrastructure.security.user;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.UserEntityRepository;
import com.employed.bar.domain.port.in.app.EmployeeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeRoleChangeService {

    private final UserEntityRepository userEntityRepository;
    private final EmployeeUseCase employeeUseCase;
    private final PasswordEncoder passwordEncoder;

    /**
     * Maneja la lógica cuando un empleado cambia de rol
     * @param employeeId El ID del empleado
     * @param newRole El nuevo rol del empleado
     * @param password La contraseña para el nuevo usuario
     */
    public void handleRoleChange(Long employeeId, EmployeeRole newRole, String password) {
        if (newRole == EmployeeRole.ADMIN) {
            // Buscar el empleado correspondiente
            Optional<EmployeeClass> employeeOptional = employeeUseCase.getEmployeeById(employeeId);

            if (employeeOptional.isPresent()) {
                EmployeeClass employee = employeeOptional.get();

                // Verificar si ya existe un usuario con el email del empleado
                Optional<UserEntity> existingUser = userEntityRepository.findByEmail(employee.getEmail());

                if (existingUser.isEmpty()) {
                    // Crear un nuevo usuario con rol ADMIN
                    UserEntity userEntity = UserEntity.builder()
                            .name(employee.getName()) // Usamos el nombre del empleado
                            .email(employee.getEmail())
                            .password(passwordEncoder.encode(password)) // Usamos la contraseña proporcionada
                            .role(newRole)
                            .build();

                    userEntityRepository.save(userEntity);
                } else {
                    // Actualizar el rol del usuario existente si es necesario
                    UserEntity user = existingUser.get();
                    if (user.getRole() != newRole) {
                        user.setRole(newRole);
                        user.setPassword(passwordEncoder.encode(password)); // Actualizar contraseña también
                        userEntityRepository.save(user);
                    }
                }
            }
        }
    }

    /**
     * Versión sobrecargada para mantener compatibilidad si se necesita
     */
    public void handleRoleChange(Long employeeId, EmployeeRole newRole) {
        // Usar una contraseña temporal si no se proporciona una
        handleRoleChange(employeeId, newRole, generateTempPassword());
    }

    private String generateTempPassword() {
        // Generar una contraseña temporal segura
        return "TempPass!" + System.currentTimeMillis();
    }
}