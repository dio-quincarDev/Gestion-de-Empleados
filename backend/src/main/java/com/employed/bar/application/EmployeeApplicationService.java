package com.employed.bar.application;

import com.employed.bar.adapters.dtos.EmployeeDto;
import com.employed.bar.domain.exceptions.EmailAlreadyExistException;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.services.AttendanceService;
import com.employed.bar.ports.in.EmployeeRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmployeeApplicationService {
    private final EmployeeRepository employeeRepository;
    private final AttendanceService attendanceService;

    public EmployeeApplicationService(EmployeeRepository employeeRepository, AttendanceService attendanceService) {
        this.employeeRepository = employeeRepository;
        this.attendanceService = attendanceService;
    }

    public Employee createEmployee(@Valid EmployeeDto employeeDto) {
        if (employeeRepository.findByEmail(employeeDto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistException("El email ya está registrado: " + employeeDto.getEmail());
        }
        Employee employee = mapToEntity(employeeDto);
        return employeeRepository.save(employee);
    }

    public Employee getEmployeeById(Long id) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isPresent()) {
            return optionalEmployee.get();
        } else {
            throw new EmployeeNotFoundException("Employee Not Found with ID" + id);
        }
    }

    public List<Employee>getEmployeeByStatus(String status) {
        return employeeRepository.findByStatus(status);
    }

    public Optional<Employee> getEmployeeByName(String name) {
        return employeeRepository.findByName(name);
    }

    public Optional<Employee> getEmployeeByRole(String role) {
        return employeeRepository.findByRole(role);
    }

    public List<Employee> getEmployees() {
        return employeeRepository.findAll();
    }

    public void deleteEmployee(Long id) {
        Employee employee = getEmployeeById(id);
        employeeRepository.delete(employee);
    }

    public double calculateAttendancePercentage(Employee employee, int year, int month, int day) {
        return attendanceService.calculateAttendancePercentage(employee, year, month, day);
    }

    public Employee updateEmployee(Long id, EmployeeDto employeeDto) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    if (!employee.getEmail().equals(employeeDto.getEmail()) &&
                            employeeRepository.findByEmail(employeeDto.getEmail()).isPresent()) {
                        throw new EmailAlreadyExistException("El email ya está registrado: " + employeeDto.getEmail());
                    }
                    mapToEntity(employeeDto, employee);
                    return employeeRepository.save(employee);
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
