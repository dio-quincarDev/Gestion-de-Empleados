package com.employed.bar.application.service;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.domain.exceptions.EmailAlreadyExistException;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.port.in.app.AttendanceUseCase;
import com.employed.bar.domain.port.in.app.EmployeeUseCase;
import com.employed.bar.domain.port.in.payment.PaymentCalculationUseCase;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class EmployeeApplicationService implements EmployeeUseCase {
    private final EmployeeRepositoryPort employeeRepositoryPort;
    private final AttendanceUseCase attendanceUseCase;
    private final PaymentCalculationUseCase paymentCalculationUseCase;

    public EmployeeApplicationService(EmployeeRepositoryPort employeeRepositoryPort, AttendanceUseCase attendanceUseCase, PaymentCalculationUseCase paymentCalculationUseCase) {
        this.employeeRepositoryPort = employeeRepositoryPort;
        this.attendanceUseCase = attendanceUseCase;
        this.paymentCalculationUseCase = paymentCalculationUseCase;
    }

    @Override
    public EmployeeClass createEmployee(EmployeeClass employee) {
        if (doesEmailExist(employee.getEmail())){
            throw new EmailAlreadyExistException("Este Email ya existe: " + employee.getEmail());
        }
        validateEmployeePaymentType(employee);
        return employeeRepositoryPort.save(employee);
    }

    @Override
    public Optional<EmployeeClass> getEmployeeById(Long id) {
        return employeeRepositoryPort.findById(id);
    }

    @Override
    public List<EmployeeClass> getEmployees() {
        return employeeRepositoryPort.findAll();
    }

    @Override
    public List<EmployeeClass> searchEmployees(String name, EmployeeRole role, EmployeeStatus status) {
        return employeeRepositoryPort.searchEmployees(name, role, status);
    }

    @Override
    public void deleteEmployee(Long id) {
        employeeRepositoryPort.findById(id).ifPresent(employeeRepositoryPort::delete);
    }

    @Override
    public EmployeeClass updateEmployee(Long id, EmployeeClass updatedEmployee) {
        return employeeRepositoryPort.findById(id)
                .map(employee -> {
                    if (!employee.getEmail().equals(updatedEmployee.getEmail()) &&
                            doesEmailExist(updatedEmployee.getEmail())) {
                        throw new EmailAlreadyExistException("Email already in use: " + updatedEmployee.getEmail());
                    }
                    validateEmployeePaymentType(updatedEmployee);
                    employee.updateWith(updatedEmployee);
                    return employeeRepositoryPort.save(employee);
                })
                .orElseThrow(()-> new EmployeeNotFoundException("Employee not Found with ID: " + id));
    }

    @Override
    public EmployeeClass updateHourlyRate(Long employeeId, java.math.BigDecimal newRate) {
        return employeeRepositoryPort.findById(employeeId)
                .map(employee -> {
                    employee.setHourlyRate(newRate);
                    return employeeRepositoryPort.save(employee);
                })
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not Found with ID: " + employeeId));
    }

    @Override
    public Optional<EmployeeClass> findByEmail(String email) {
        return employeeRepositoryPort.findByEmail(email);
    }

    public double calculateAttendancePercentage(EmployeeClass employee, int year, int month, int day) {
        return attendanceUseCase.calculateAttendancePercentage(employee.getId(), year, month, day);
    }

    public boolean doesEmailExist(String email) {
        return employeeRepositoryPort.findByEmail(email).isPresent();
    }

    private void validateEmployeePaymentType(EmployeeClass employee) {
        if (employee.getPaymentType() == null) {
            throw new IllegalArgumentException("Payment type cannot be null");
        }
        switch (employee.getPaymentType()) {
            case HOURLY:
                if (employee.getHourlyRate() == null || employee.getHourlyRate().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Hourly rate must be positive for hourly employees.");
                }
                if (employee.getSalary() != null && employee.getSalary().compareTo(BigDecimal.ZERO) != 0) {
                    throw new IllegalArgumentException("Salary must be zero for hourly employees.");
                }
                break;
            case SALARIED:
                if (employee.getSalary() == null || employee.getSalary().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Salary must be positive for salaried employees.");
                }
                if (employee.isPaysOvertime() && (employee.getHourlyRate() == null || employee.getHourlyRate().compareTo(BigDecimal.ZERO) <= 0)) {
                    throw new IllegalArgumentException("Hourly rate must be positive for salaried employees who are paid overtime.");
                }
                break;
        }
    }

    @Override
    public BigDecimal calculateEmployeePay(Long employeeId, double regularHours, double overtimeHours) {
      EmployeeClass employee = employeeRepositoryPort.findById(employeeId)
              .orElseThrow(()-> new EmployeeNotFoundException("Employee Not Found"));
      return paymentCalculationUseCase.calculateTotalPay(
              employee.getPaymentType(),
              employee.getSalary(),
              employee.getHourlyRate(),
              employee.isPaysOvertime(),
              employee.getOvertimeRateType(),
              regularHours,
              overtimeHours
      );
    }


}
