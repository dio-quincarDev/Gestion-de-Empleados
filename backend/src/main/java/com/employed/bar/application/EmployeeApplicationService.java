package com.employed.bar.application;

import com.employed.bar.adapters.dtos.EmployeeDto;
import com.employed.bar.domain.exceptions.EmailAlreadyExistException;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.services.AttendanceService;
import com.employed.bar.ports.out.EmployeeRepositoryPort;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmployeeApplicationService {
    private final EmployeeRepositoryPort employeeRepositoryPort;
    private final AttendanceService attendanceService;

    public EmployeeApplicationService(EmployeeRepositoryPort employeeRepositoryPort, AttendanceService attendanceService, AttendanceService attendanceService1) {
        this.employeeRepositoryPort = employeeRepositoryPort;
        this.attendanceService = attendanceService1;
    }

    public Employee createEmployee(@Valid EmployeeDto employeeDto) {
        if (employeeRepositoryPort.findByEmail(employeeDto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistException("El email ya está registrado: " + employeeDto.getEmail());
        }
        Employee employee = mapToEntity(employeeDto);
        return employeeRepositoryPort.save(employee);
    }

    public Employee getEmployeeById(Long id) {
        Optional<Employee> optionalEmployee = employeeRepositoryPort.findById(id);
        if (optionalEmployee.isPresent()) {
            return optionalEmployee.get();
        } else {
            throw new EmployeeNotFoundException("Employee Not Found with ID" + id);
        }
    }

    public List<Employee>getEmployeeByStatus(String status) {
        return employeeRepositoryPort.findByStatus(status);
    }

    public Optional<Employee> getEmployeeByName(String name) {
        return employeeRepositoryPort.findByName(name);
    }

    public Optional<Employee> getEmployeeByRole(String role) {
        return employeeRepositoryPort.findByRole(role);
    }

    public List<Employee> getEmployees() {
        return employeeRepositoryPort.findAll();
    }

    public void deleteEmployee(Long id) {
        Employee employee = getEmployeeById(id);
        employeeRepositoryPort.delete(employee);
    }

    public double calculateAttendancePercentage(Employee employee, int year, int month, int day) {
        return attendanceService.calculateAttendancePercentage(employee, year, month, day);
    }

    public Employee updateEmployee(Long id, EmployeeDto employeeDto) {
        return employeeRepositoryPort.findById(id)
                .map(employee -> {
                    if (!employee.getEmail().equals(employeeDto.getEmail()) &&
                            employeeRepositoryPort.findByEmail(employeeDto.getEmail()).isPresent()) {
                        throw new EmailAlreadyExistException("El email ya está registrado: " + employeeDto.getEmail());
                    }
                    mapToEntity(employeeDto, employee);
                    return employeeRepositoryPort.save(employee);
                })
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not Found with ID" + id));
    }

    private Employee mapToEntity(EmployeeDto employeeDto) {
        Employee employee = new Employee();
        employee.setName(employeeDto.getName());
        employee.setRole(employeeDto.getRole());
        employee.setStatus(employeeDto.getStatus());
        employee.setEmail(employeeDto.getEmail());
        employee.setSalary(employeeDto.getSalary());


        return employee;
    }

    private void mapToEntity(EmployeeDto employeeDto, Employee employee) {
        employee.setName(employeeDto.getName());
        employee.setRole(employeeDto.getRole());
        employee.setStatus(employeeDto.getStatus());
        employee.setEmail(employeeDto.getEmail());
        employee.setSalary(employeeDto.getSalary());



    }

}
