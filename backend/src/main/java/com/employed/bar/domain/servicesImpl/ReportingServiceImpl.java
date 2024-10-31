package com.employed.bar.domain.servicesImpl;

import com.employed.bar.adapters.dtos.AttendanceReportDto;
import com.employed.bar.adapters.dtos.ConsumptionReportDto;
import com.employed.bar.adapters.dtos.ReportDto;
import com.employed.bar.adapters.integrations.EmailService;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ReportingServiceImpl implements ReportingService {
    private final static Logger logger = LoggerFactory.getLogger(ReportingServiceImpl.class);

    private final ConsumptionRepository consumptionRepository;
    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final EmailService emailService;

    public ReportingServiceImpl(ConsumptionRepository consumptionRepository, AttendanceRepository attendanceRepository,
                                EmployeeRepository employeeRepository, EmailService emailService) {
        this.consumptionRepository = consumptionRepository;
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
        this.emailService = emailService;
    }


    @Override
    public ReportDto generateCompleteReport(LocalDate startDate, LocalDate endDate, Long employeeId) {
        logger.info("Generating complete report for dates {} to {}, employee ID: {}", startDate, endDate, employeeId);

        List<Employee> employees = getEmployees(employeeId);
        List<AttendanceReportDto> attendanceReports = new ArrayList<>();
        List<ConsumptionReportDto> consumptionReports = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            attendanceReports.addAll(generateAttendanceReports(employees, currentDate));
            consumptionReports.addAll(generateConsumptionReports(employees, currentDate));
            currentDate = currentDate.plusDays(1);
        }

        double totalAttendancePercentage = attendanceReports.stream().mapToDouble(AttendanceReportDto::getAttendancePercentage).sum();
        double totalConsumption = consumptionReports.stream().mapToDouble(report -> report.getAmount().doubleValue()).sum();

        return new ReportDto(attendanceReports, consumptionReports, totalAttendancePercentage, totalConsumption);

    }

    private List<Employee> getEmployees(Long employeeId) {
        if (employeeId != null) {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));
            return List.of(employee);
        }
        return employeeRepository.findAll();
    }


    @Override
    public void sendWeeklyReports() {
        List<Employee> employees = employeeRepository.findAll();

        for (Employee employee : employees) {
            ReportDto report = generateCompleteReport(LocalDate.now(), null, employee.getId());


            String emailBody = emailService.generateEmailBody(report, employee);
            String emailSubject = "Weekly Report for " + employee.getName();

            emailService.sendHtmlMessage(employee.getEmail(), emailSubject, emailBody);
        }
    }

    @Override
    public void sendTestEmail() {
        List<Employee> employees = employeeRepository.findAll();
        for (Employee employee : employees) {
            ReportDto report = generateCompleteReport(LocalDate.now(), null, employee.getId());
            System.out.println("Report for employee " + employee.getName() + ": " + report.toString());
            String emailBody = emailService.generateEmailBody(report, employee);
            System.out.println("Email body for employee " + employee.getName() + ": " + emailBody);
            String emailSubject = "Test Email for " + employee.getName();
            emailService.sendHtmlMessage(employee.getEmail(), emailSubject, emailBody);

        }
    }

    @Override
    public void sendBulkEmails(List<Employee> employees, List<ReportDto> reports) {
        if (employees.size() != reports.size()) {
            throw new IllegalArgumentException("Number of employees and reports must be equal");
        }
        List<CompletableFuture<Void>> futures = IntStream.range(0, employees.size())
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    try {
                        Employee employee = employees.get(i);
                        ReportDto report = reports.get(i);
                        String emailBody = emailService.generateEmailBody(report, employee);
                        emailService.sendHtmlMessage(employee.getEmail(), "Weekly Report", emailBody);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

    }

    @Override
    public void sendTestBulkEmails() {
        List<Employee> employees = employeeRepository.findAll().subList(0, 5);  // Por ejemplo, seleccionar los primeros 5 empleados para la prueba.
        List<ReportDto> reports = employees.stream()
                .map(employee -> generateCompleteReport(LocalDate.now(), null, employee.getId()))
                .collect(Collectors.toList());

        employees.forEach(employee -> logger.info("Sending test email to " + employee.getEmail()));
        sendBulkEmails(employees, reports);  // Reutilizando el mismo método
    }


    private List<AttendanceReportDto> generateAttendanceReports(List<Employee> employees, LocalDate date) {
        logger.debug("Generating attendance reports for {} employees", employees.size());
        return employees.stream()
                .flatMap(employee -> attendanceRepository.findByEmployeeAndDate(employee, date).stream()
                        .map(record -> {
                            double workedHours = calculateWorkedHours(record);
                            double attendancePercentage = calculateAttendancePercentage(record);
                            return new AttendanceReportDto(
                                    employee.getName(),
                                    record.getDate(),
                                    record.getEntryTime(),
                                    record.getExitTime(),
                                    workedHours,
                                    attendancePercentage
                            );
                        }))
                .collect(Collectors.toList());
    }

    private double calculateWorkedHours(AttendanceRecord record) {
        LocalTime entryTime = record.getEntryTime();
        LocalTime exitTime = record.getExitTime();

        // Si no hay hora de salida, asumimos que el empleado sigue trabajando y tomamos la hora actual.
        if (exitTime == null) {
            exitTime = LocalTime.now();
        }

        LocalDate entryDate = record.getDate();
        LocalDateTime entryDateTime = entryDate.atTime(entryTime);
        LocalDateTime exitDateTime = entryDate.atTime(exitTime);

        // Si `exitTime` es antes de `entryTime`, significa que el turno cruza la medianoche.
        if (exitTime.isBefore(entryTime)) {
            exitDateTime = exitDateTime.plusDays(1);
        }

        // Calcula la duración y convierte a horas decimales.
        Duration duration = Duration.between(entryDateTime, exitDateTime);
        double hours = duration.toHours() + (duration.toMinutesPart() / 60.0); // Horas decimales

        logger.debug("Calculated worked hours for entry: {}, exit: {} is {}", entryDateTime, exitDateTime, hours);
        return hours;
    }

private double calculateAttendancePercentage(AttendanceRecord record) {

        final double FULL_WORKDAY_HOURS = 8.0;
        double workedHours = calculateWorkedHours(record);

        workedHours = Math.min(workedHours,FULL_WORKDAY_HOURS );
        double percentage = (workedHours / FULL_WORKDAY_HOURS) * 100;

            return Math.round(percentage * 100.0) / 100.0;
        }


    private List<ConsumptionReportDto> generateConsumptionReports(List<Employee> employees, LocalDate date) {
        logger.debug("Generating consumption reports for {} employees", employees.size());
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return employees.stream()
                .flatMap(employee -> {
                    try {
                        List<Consumption> consumptions = consumptionRepository.findByEmployeeAndDateTimeBetween(employee, startOfDay, endOfDay, null);
                        return consumptions.stream()
                                .map(consumption -> new ConsumptionReportDto(
                                        employee.getName(),
                                        consumption.getConsumptionDate(),
                                        consumption.getAmount(),
                                        consumption.getDescription()
                                ));
                    } catch (Exception e) {
                        logger.error("Error generating consumption report for employee {}", employee.getId(), e);
                        return null;
                    }
                })
                .filter(report -> report != null)
                .collect(Collectors.toList());
    }

}
