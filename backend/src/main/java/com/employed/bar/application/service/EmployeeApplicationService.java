package com.employed.bar.application.service;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.exceptions.EmailAlreadyExistException;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.ports.in.service.AttendanceUseCase;
import com.employed.bar.domain.ports.in.service.EmployeeUseCase;
import com.employed.bar.domain.ports.out.EmployeeRepositoryPort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmployeeApplicationService implements EmployeeUseCase {
    private final EmployeeRepositoryPort employeeRepositoryPort;
    private final AttendanceUseCase attendanceUseCase;

    public EmployeeApplicationService(EmployeeRepositoryPort employeeRepositoryPort, AttendanceUseCase attendanceUseCase) {
        this.employeeRepositoryPort = employeeRepositoryPort;
        this.attendanceUseCase = attendanceUseCase;
    }

    @Override
    public Employee createEmployee(Employee employee) {
        if (employeeRepositoryPort.findByEmail(employee.getEmail()).isPresent()){
            throw new EmailAlreadyExistException("Este Email ya existe: " + employee.getEmail());
        }
        return employeeRepositoryPort.save(employee);
    }

    @Override
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepositoryPort.findById(id);
    }

    @Override
    public Optional<Employee> getEmployeeByName(String name) {
        return employeeRepositoryPort.findByName(name);
    }

    @Override
    public Optional<Employee> getEmployeeByRole(EmployeeRole role) {
        return employeeRepositoryPort.findByRole(role);
    }

    @Override
    public List<Employee> getEmployeeByStatus(String status) {
        return employeeRepositoryPort.findByStatus(status);
    }

    @Override
    public List<Employee> getEmployees() {
        return employeeRepositoryPort.findAll();
    }

    @Override
    public void deleteEmployee(Long id) {
        employeeRepositoryPort.findById(id).ifPresent(employeeRepositoryPort::delete);
    }

    @Override
    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        return employeeRepositoryPort.findById(id)
                .map(employee -> {
                    if (!employee.getEmail().equals(updatedEmployee.getEmail()) &&
                            employeeRepositoryPort.findByEmail(updatedEmployee.getEmail()).isPresent()) {
                        throw new EmailAlreadyExistException("Email already in use: " + updatedEmployee.getEmail());
                    }
                    employee.setName(updatedEmployee.getName());
                    employee.setRole(updatedEmployee.getRole());
                    employee.setStatus(updatedEmployee.getStatus());
                    employee.setEmail(updatedEmployee.getEmail());
                    employee.setSalary(updatedEmployee.getSalary());
                    return employeeRepositoryPort.save(employee);
                })
                .orElseThrow(()-> new EmployeeNotFoundException("Employee not Found with ID: " + id));
    }

    @Override
    public Optional<Employee> findByEmail(String email) {
        return employeeRepositoryPort.findByEmail(email);
    }

    public double calculateAttendancePercentage(Employee employee, int year, int month, int day) {
        return attendanceUseCase.calculateAttendancePercentage(employee, year, month, day);
    }
}
