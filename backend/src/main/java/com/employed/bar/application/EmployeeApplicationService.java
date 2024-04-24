package com.employed.bar.application;

import com.employed.bar.adapters.dtos.EmployeeDto;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.ports.in.AttendanceCalculationService;
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
    private final AttendanceCalculationService attendanceCalculationService;

    public EmployeeApplicationService(EmployeeRepository employeeRepository, AttendanceCalculationService attendanceCalculationService) {
        this.employeeRepository = employeeRepository;
        this.attendanceCalculationService = attendanceCalculationService;
    }
    public Employee createEmployee(@Valid EmployeeDto employee){
        return employeeRepository.save(employee);
    }

    public Employee getEmployeeById(Long id){
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if(optionalEmployee.isPresent()){
            return optionalEmployee.get();
        }else{
            throw new EmployeeNotFoundException("Employee Not Found with ID" + id);
        }
    }

    public List<Employee> getEmployees(){
    return employeeRepository.findAll();
    }

    public void deleteEmployee(Long id){
        Employee employee = getEmployeeById(id);
        employeeRepository.delete(employee);
    }
    public double calculateAttendancePercentage(Employee employee, int year, int month, int day){
        return attendanceCalculationService.calculateAttendancePercentage(employee, year, month, day);
    }

    public Employee updateEmployee(Long id, EmployeeDto employeeDto) {
        Employee existingEmployee = getEmployeeById(id);

        existingEmployee.setName(employeeDto.getName());
        existingEmployee.setRole(employeeDto.getRole());
        existingEmployee.setSalary(employeeDto.getSalary());
        
        return employeeRepository.save(existingEmployee);
    }
}
