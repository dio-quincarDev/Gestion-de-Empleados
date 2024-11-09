package com.employed.bar.domain.servicesImpl;

import com.employed.bar.adapters.dtos.AttendanceDto;
import com.employed.bar.adapters.dtos.AttendanceReportDto;
import com.employed.bar.domain.exceptions.InvalidScheduleException;
import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.model.Schedule;
import com.employed.bar.domain.services.AttendanceService;
import com.employed.bar.ports.in.EmployeeRepository;
import com.employed.bar.domain.exceptions.InvalidAttendanceDataException;
import com.employed.bar.ports.in.ScheduleRepository;
import com.employed.bar.ports.out.AttendanceRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final ScheduleRepository scheduleRepository;


    public AttendanceServiceImpl(EmployeeRepository employeeRepository,
                                 AttendanceRepository attendanceRepository,
                                 ScheduleRepository scheduleRepository) {
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public AttendanceRecord registerAttendance(AttendanceDto attendanceDto) {
        Long employeeId = attendanceDto.getEmployeeId();
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new InvalidAttendanceDataException("Employee Not Found"));

        AttendanceRecord attendanceRecord = new AttendanceRecord(
                null,
                employee,
                attendanceDto.getDate(),
                attendanceDto.getEntryTime(),
                attendanceDto.getExitTime(),
                attendanceDto.getStatus()
        );

        return attendanceRepository.save(attendanceRecord);
    }

    private void validateAttendanceDto(AttendanceDto attendanceDto) {
        if (attendanceDto.getEmployeeId() == null || attendanceDto.getEmployeeId() == null) {
            throw new InvalidAttendanceDataException("Employee information is missing");
        }
        if (attendanceDto.getEntryTime() == null) {
            throw new InvalidAttendanceDataException("Entry time is missing");
        }
        if (attendanceDto.getStatus() == null || attendanceDto.getStatus().isEmpty()) {
            throw new InvalidAttendanceDataException("Attendance status is missing");
        }
    }

    @Override
    public List<AttendanceRecord> findEmployeeAttendances(Employee employee, LocalDate date) {
        return attendanceRepository.findByEmployeeAndDate(employee, date);
    }

    @Override
    public double calculateAttendancePercentage(Employee employee, int year, int month, int day) {
        if (year < 1 || month > 12 || day < 1 || day > 31) {
            throw new IllegalArgumentException("Invalid date");
        }
        List<AttendanceRecord> records = attendanceRepository.findAttendanceRecords(year, month, day);
        long totalWorkingMinutes = 0;
        for (AttendanceRecord record : records) {
            long entryMinute = record.getEntryTime().getHour() * 60 + record.getEntryTime().getMinute();
            long exitMinute = record.getExitTime().getHour() * 60 + record.getExitTime().getMinute();
            totalWorkingMinutes += exitMinute - entryMinute;
        }
        // Buscar el horario del empleado para la fecha especificada
        Optional<List<Schedule>> optionalSchedules = Optional.ofNullable(scheduleRepository.findByEmployeeAndDate(employee,
                LocalDateTime.of(year, month, day, 0, 0),
                LocalDateTime.of(year, month, day, 23, 59)));

        // Si no se encuentran horarios, consideramos que el empleado no tenía que trabajar ese día
        if (!optionalSchedules.isPresent()) {
            return 0.0;
        }

        // Extraer el primer horario de la lista opcional (asumiendo que haya al menos uno)
        Schedule schedule = optionalSchedules.get().get(0);

        long scheduleMinutes = Duration.between(schedule.getStartTime(), schedule.getEndTime()).toMinutes();

        // Validar que el horario tenga sentido (hora de inicio menor a hora de fin)
        if (scheduleMinutes <= 0) {
            throw new InvalidScheduleException("El horario del empleado es inválido");
        }

        return (double) totalWorkingMinutes / scheduleMinutes * 100;
    }


    private long calculateWorkingMinutesForSchedule(@NotNull Schedule schedule) {
        LocalDateTime start = schedule.getStartTime();
        LocalDateTime end = schedule.getEndTime();
        if (end.isBefore(start) || end.isEqual(start)) {
            throw new InvalidScheduleException("End time must be after start time");
        }
        return Duration.between(start, end).toMinutes();
    }

    @Override
    public List<AttendanceReportDto> generateAttendanceReport(int year, int month, int day) {
        LocalDate reportDate = LocalDate.of(year, month, day);
        List<AttendanceRecord> attendanceRecords = attendanceRepository.findAttendanceRecords(year, month, day);

        Map<Employee, List<AttendanceRecord>> recordsByEmployee = attendanceRecords.stream()
                .collect(Collectors.groupingBy(AttendanceRecord::getEmployee));

        List<AttendanceReportDto> report = new ArrayList<>();

        for (Map.Entry<Employee, List<AttendanceRecord>> entry : recordsByEmployee.entrySet()) {
            Employee employee = entry.getKey();
            List<AttendanceRecord> records = entry.getValue();

            AttendanceReportDto dto = new AttendanceReportDto();
            dto.setEmployeeName(employee.getName());
            dto.setAttendanceDate(reportDate);

            double totalWorkingHours = records.stream()
                    .mapToDouble(record -> calculateWorkedHours(record))
                    .sum();

            dto.setWorkedHours(totalWorkingHours);
            // Si necesitas añadir información del horario, puedes hacerlo aquí
            Schedule schedule = scheduleRepository.findByEmployeeAndDate(employee,
                    LocalDateTime.of(year, month, day, 0, 0),
                    LocalDateTime.of(year, month, day, 23, 59)).stream().findFirst().orElse(null);

            if (schedule != null) {
                Duration scheduleDuration = Duration.between(schedule.getStartTime(), schedule.getEndTime());
                double scheduleHours = scheduleDuration.toHours() + (scheduleDuration.toMinutesPart() / 60.0);
                double attendancePercentage = (totalWorkingHours / scheduleHours) * 100;
                dto.setAttendancePercentage(attendancePercentage);
            }

            report.add(dto);
        }

        return report;
    }


    // Cambia el método para calcular horas trabajadas en horas decimales
    private double calculateWorkedHours(AttendanceRecord record) {
        LocalTime entryTime = record.getEntryTime();
        LocalTime exitTime = record.getExitTime();

        // Maneja caso de salida nula o igual a medianoche
        if (exitTime == null) {
            exitTime = LocalTime.MIDNIGHT; // Ajusta a fin de día para simplificar el cálculo
        }
        LocalDate entryDate = record.getDate();
        LocalDateTime entryDateTime = entryDate.atTime(entryTime);
        LocalDateTime exitDateTime = entryDate.atTime(exitTime);

        if (exitTime.isBefore(entryTime)) {
            // Ajuste para manejar el cruce de medianoche
            exitDateTime = exitDateTime.plusDays(1);
        }

        Duration duration = Duration.between(entryTime, exitTime);
        double hours = duration.toHours() + (duration.toMinutesPart() / 60.0); // Convierte a horas decimales
        return hours;
    }

    @Override
    public List<AttendanceRecord> getAttendanceListByEmployeeAndDateRange(Employee employee, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByEmployeeAndDateRange(employee, startDate, endDate);
    }

}

