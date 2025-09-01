package com.employed.bar.domain.servicesImpl;


import com.employed.bar.domain.exceptions.EmailAlreadyExistException;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.services.EmployeeService;
import com.employed.bar.ports.out.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Employee createEmployee(Employee employee) {
        if (employeeRepository.findByEmail(employee.getEmail()).isPresent()){
            throw new EmailAlreadyExistException("Este Email ya existe" + employee.getEmail());
        }
        return employeeRepository.save(employee);
    }

    @Override
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    @Override
    public Optional<Employee> getEmployeeByName(String name) {
        return employeeRepository.findByName(name);
    }

    @Override
    public Optional<Employee> getEmployeeByRole(String role) {
        return employeeRepository.findByRole(role);
    }

    @Override
    public List<Employee> getEmployeeByStatus(String status) {
        return employeeRepository.findByStatus(status);
    }

    @Override
    public List<Employee> getEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public void deleteEmployee(Long id) {
        employeeRepository.findById(id).ifPresent(employeeRepository::delete);
    }

    @Override
    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        return employeeRepository.findById(id)
               .map(employee -> {
                   if (!employee.getEmail().equals(updatedEmployee.getEmail()) &&
                           employeeRepository.findByEmail(updatedEmployee.getEmail()).isPresent()) {
                       throw new EmailAlreadyExistException("Email already in use: " + updatedEmployee.getEmail());
                   }
                   employee.setName(updatedEmployee.getName());
                   employee.setRole(updatedEmployee.getRole());
                   employee.setStatus(updatedEmployee.getStatus());
                   employee.setEmail(updatedEmployee.getEmail());
                   return employeeRepository.save(employee);
               })
                .orElseThrow(()-> new EmployeeNotFoundException("Employee not Found with ID" + id));
    }

    @Override
    public Optional<Employee> findByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }


}
