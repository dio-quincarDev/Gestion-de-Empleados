package com.employed.bar.application;

import com.employed.bar.adapters.dtos.ConsumptionDto;
import com.employed.bar.adapters.dtos.ConsumptionReportDto;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.Consumption;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.services.ConsumptionService;
import com.employed.bar.ports.in.EmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ConsumptionApplicationService {

    private final ConsumptionService consumptionService;
    private final EmployeeRepository employeeRepository;



    public Consumption processConsumption(ConsumptionDto consumptionDto) {
        Employee employee = employeeRepository.findById(consumptionDto.getEmployeeId())
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + consumptionDto.getEmployeeId()));

        // Mapea los datos del DTO a la entidad Consumption
        Consumption consumption = new Consumption();
        consumption.setEmployee(employee);
        consumption.setAmount(consumptionDto.getAmount());
        consumption.setConsumptionDate(consumptionDto.getDate());
        consumption.setDescription(consumptionDto.getDescription());

        return consumptionService.createConsumption(consumption);
    }

    public Consumption createConsumption(Consumption consumption) {
        return consumptionService.createConsumption(consumption);
    }

    public Optional<Consumption> getConsumptionById(Long id) {
        return consumptionService.getConsumptionById(id);
    }

    public List<ConsumptionReportDto> getConsumptionsByEmployee(Employee employee, LocalDateTime startDate,
                                                                LocalDateTime endDate, String description) {
                return consumptionService.getConsumptionByEmployee(employee, startDate, endDate, description);
    }

    public BigDecimal calculateTotalConsumptionByEmployee(Employee employee, LocalDateTime startDate, LocalDateTime endDate) {
        return consumptionService.calculateTotalConsumptionByEmployee(employee, startDate, endDate);
    }

    // Calcular el consumo total de un empleado
    public BigDecimal calculateTotalConsumptionByEmployee(Long employeeId, LocalDate startDate, LocalDate endDate) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return consumptionService.calculateTotalConsumptionByEmployee(employee, startDateTime, endDateTime);
    }

    // Calcular el consumo total de todos los empleados
    public BigDecimal calculateTotalConsumptionForAllEmployees(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return consumptionService.calculateTotalConsumptionForAllEmployees(startDateTime, endDateTime);
    }

    public void deleteConsumption(Long id) {
        consumptionService.deleteConsumption(id);
    }
}
