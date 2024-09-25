package com.employed.bar.domain.servicesImpl;

import com.employed.bar.adapters.dtos.AttendanceReportDto;
import com.employed.bar.adapters.dtos.ConsumptionReportDto;
import com.employed.bar.adapters.dtos.ReportDto;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.exceptions.ReportGenerationException;
import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Consumption;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.services.ReportingService;
import com.employed.bar.ports.in.ConsumptionRepository;
import com.employed.bar.ports.in.EmployeeRepository;
import com.employed.bar.ports.out.AttendanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import java.util.stream.Collectors;

@Service
public class ReportingServiceImpl implements ReportingService {
    private final static Logger logger = LoggerFactory.getLogger(ReportingServiceImpl.class);

    private final ConsumptionRepository consumptionRepository;
    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public ReportingServiceImpl(ConsumptionRepository consumptionRepository, AttendanceRepository attendanceRepository, EmployeeRepository employeeRepository) {
        this.consumptionRepository = consumptionRepository;
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
    }


    @Override
    public ReportDto generateCompleteReport(LocalDate date, Long employeeId) {
        logger.info("Generating complete report for dates {} to {}, employee ID: {}", date, employeeId);

        List<Employee> employees;
        try {
            if (employeeId != null) {
                Employee employee = employeeRepository.findById(employeeId)
                        .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));
                employees = List.of(employee);
            } else {
                employees = employeeRepository.findAll();
            }
        } catch (Exception e) {
            logger.error("Error retrieving employees", e);
            throw new ReportGenerationException("Error retrieving employees");
        }
        // Convertir LocalDate a un rango de LocalDateTime
        //LocalDateTime startDate = date.atStartOfDay(); // 00:00 del día especificado
        //LocalDateTime endDate = date.atTime(LocalTime.MAX); // 23:59:59.999999999 del mismo día

        List<AttendanceReportDto> attendanceReports = generateAttendanceReports(employees, date);
        List<ConsumptionReportDto> consumptionReports = generateConsumptionReports(employees, date);

        logger.info("Report generation completed successfully");
        return new ReportDto(attendanceReports, consumptionReports);
    }

    private List<AttendanceReportDto> generateAttendanceReports(List<Employee> employees, LocalDate date) {
        logger.debug("Generating attendance reports for {} employees", employees.size());
        return employees.stream()
                .flatMap(employee -> {
                    try {
                        List<AttendanceRecord> records = attendanceRepository.findByEmployeeAndDate(employee, date);
                        return  records.stream().map(record -> new AttendanceReportDto(
                                employee.getName(),
                                date,
                                record.getEntryTime(),
                                record.getExitTime(),
                                calculateAttendancePercentage(record)

                        ));

                    } catch (Exception e) {
                        logger.error("Error generating attendance report for employee {}", employee.getId(), e);
                        return null;
                    }
                })
                .filter(report -> report != null)
                .collect(Collectors.toList());
    }

    private List<ConsumptionReportDto> generateConsumptionReports(List<Employee> employees, LocalDate date) {
        logger.debug("Generating consumption reports for {} employees", employees.size());
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return employees.stream()
                .flatMap(employee -> {
                    try {
                        List<Consumption> consumptions = consumptionRepository.findByEmployeeAndDateTimeBetween(employee, startOfDay, endOfDay);
                        return consumptions.stream()
                                .map(consumption -> new ConsumptionReportDto(
                                        employee.getName(),
                                        consumption.getConsumptionDate(),
                                        consumption.getAmount()
                                ));
                    } catch (Exception e) {
                        logger.error("Error generating consumption report for employee {}", employee.getId(), e);
                        return null;
                    }
                })
                .filter(report -> report != null)
                .collect(Collectors.toList());
    }

    private double calculateAttendancePercentage(AttendanceRecord record) {
        // Definimos la duración de una jornada laboral completa en minutos (8 horas)
        final int FULL_WORKDAY_MINUTES = 8 * 60;

        LocalTime entryTime = record.getEntryTime();
        LocalTime exitTime = record.getExitTime();

        // Si no hay hora de salida, asumimos que el empleado aún está trabajando
        if (exitTime == null) {
            exitTime = LocalTime.now();
        }

        // Calculamos la duración de la presencia en minutos
        long minutesPresent = java.time.Duration.between(entryTime, exitTime).toMinutes();

        // Limitamos el máximo a FULL_WORKDAY_MINUTES para no exceder el 100%
        minutesPresent = Math.min(minutesPresent, FULL_WORKDAY_MINUTES);

        // Calculamos el porcentaje
        double percentage = (double) minutesPresent / FULL_WORKDAY_MINUTES * 100;

        // Redondeamos a dos decimales
        return Math.round(percentage * 100.0) / 100.0;
    }
}
