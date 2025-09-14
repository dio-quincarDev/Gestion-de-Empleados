package com.employed.bar.application.service;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.domain.enums.OvertimeRateType;
import com.employed.bar.domain.exceptions.EmailAlreadyExistException;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.payment.AchPaymentMethod;
import com.employed.bar.domain.model.strucuture.EmployeeClass;
import com.employed.bar.domain.model.payment.PaymentMethod;
import com.employed.bar.domain.model.payment.YappyPaymentMethod;
import com.employed.bar.domain.port.in.service.AttendanceUseCase;
import com.employed.bar.domain.port.in.service.EmployeeUseCase;
import com.employed.bar.domain.port.in.service.PaymentCalculationUseCase;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
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
        if (employeeRepositoryPort.findByEmail(employee.getEmail()).isPresent()){
            throw new EmailAlreadyExistException("Este Email ya existe: " + employee.getEmail());
        }
        validatePaymentMethod(employee.getPaymentMethod());
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
        validatePaymentMethod(updatedEmployee.getPaymentMethod());
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
                    employee.setHourlyRate(updatedEmployee.getHourlyRate());
                    employee.setPaymentMethod(updatedEmployee.getPaymentMethod());
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

    @Override
    public BigDecimal calculateEmployeePay(Long employeeId, double regularHours, double overtimeHours) {
      EmployeeClass employee = employeeRepositoryPort.findById(employeeId)
              .orElseThrow(()-> new EmployeeNotFoundException("Employee Not Found"));
      BigDecimal hourlyRate = employee.getHourlyRate();
      boolean paysOvertime = employee.isPaysOvertime();
      OvertimeRateType overtimeRateType = employee.getOvertimeRateType();
      return paymentCalculationUseCase.calculateTotalPay(
              hourlyRate,
              paysOvertime,
              overtimeRateType,
              regularHours,
              overtimeHours
      );
    }

    private void validatePaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method cannot be null.");
        }
        if (paymentMethod instanceof AchPaymentMethod ach) {
            if (!StringUtils.hasText(ach.getBankName())) {
                throw new IllegalArgumentException("Bank name is required for ACH payment method.");
            }
            if (!StringUtils.hasText(ach.getAccountNumber())) {
                throw new IllegalArgumentException("Account number is required for ACH payment method.");
            }
            if (ach.getBankAccountType() == null) {
                throw new IllegalArgumentException("Bank account type is required for ACH payment method.");
            }
        } else if (paymentMethod instanceof YappyPaymentMethod yappy) {
            if (!StringUtils.hasText(yappy.getPhoneNumber())) {
                throw new IllegalArgumentException("Phone number is required for Yappy payment method.");
            }
        }
        // No validation needed for CashPaymentMethod
    }
}
