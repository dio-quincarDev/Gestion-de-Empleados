package com.employed.bar.adapters.controllers;

import com.employed.bar.adapters.dtos.EmployeeDto;
import com.employed.bar.application.EmployeeApplicationService;
import com.employed.bar.domain.model.Employee;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeApplicationService employeeApplicationService;
    public EmployeeController(EmployeeApplicationService employeeApplicationService) {
        this.employeeApplicationService = employeeApplicationService;
    }
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody @Valid EmployeeDto employeeDto){
        Employee createdEmployee = employeeApplicationService.createEmployee(employeeDto);
        return ResponseEntity.ok(createdEmployee);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee>getEmployee(@PathVariable Long id){
        Employee employee = employeeApplicationService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getEmployees(){
        List<Employee> employees = employeeApplicationService.getEmployees();
        return ResponseEntity.ok(employees);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody @Valid EmployeeDto employeeDto){
        Employee updatedEmployee = employeeApplicationService.updateEmployee(id, employeeDto);
        return ResponseEntity.ok(updatedEmployee);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void>deleteEmployee(@PathVariable Long id){
        employeeApplicationService.deleteEmployee(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
