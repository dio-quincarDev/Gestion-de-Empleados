package com.employed.bar.adapters.controllers;

import com.employed.bar.adapters.dtos.EmployeeDto;
import com.employed.bar.application.EmployeeApplicationService;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.Employee;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<Employee>getEmployeeById(@PathVariable Long id){
     try{
         Employee employee = employeeApplicationService.getEmployeeById(id);
         return ResponseEntity.ok(employee);
     }catch(EmployeeNotFoundException ex){
         return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
     }
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees(){
        List<Employee> employees = employeeApplicationService.getEmployees();
        return ResponseEntity.ok(employees);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody @Valid EmployeeDto employeeDto){
        try{
            Employee updatedEmployee = employeeApplicationService.updateEmployee(id, employeeDto);
            return ResponseEntity.ok(updatedEmployee);
        }catch(EmployeeNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>deleteEmployee(@PathVariable Long id) {
        try {
            employeeApplicationService.deleteEmployee(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (EmployeeNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{search}")
    public ResponseEntity<?>getEmployeeByStatus(@RequestParam(required = false)String status,
                                                             @RequestParam(required = false)String name,
                                                             @RequestParam(required = false)String role){
        if(status != null){
            List<Employee> employees = employeeApplicationService.getEmployeeByStatus(status);
            return ResponseEntity.ok(employees);
        } else if(name != null){
            Optional<Employee> employee = employeeApplicationService.getEmployeeByName(name);
            return employee.map(ResponseEntity::ok).orElseGet(()->ResponseEntity.notFound().build());
        } else if(role != null){
            Optional<Employee> employee = employeeApplicationService.getEmployeeByRole(role);
            return employee.map(ResponseEntity::ok).orElseGet(()->ResponseEntity.notFound().build());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }
}
