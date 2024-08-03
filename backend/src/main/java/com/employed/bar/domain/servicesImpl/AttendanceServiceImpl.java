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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        Employee employee = employeeRepository.findById(attendanceDto.getEmployee().getId())
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

    private void validateAttendanceDto(AttendanceDto dto) {
        if (dto.getEmployee() == null || dto.getEmployee().getId() == null) {
            throw new InvalidAttendanceDataException("Employee information is missing");
        }
        if (dto.getEntryTime() == null) {
            throw new InvalidAttendanceDataException("Entry time is missing");
        }
        if (dto.getStatus() == null || dto.getStatus().isEmpty()) {
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
        List<AttendanceRecord> records = attendanceRepository.findAttendanceRecords( year, month, day);
        long totalWorkingMinutes = 0;
        for (AttendanceRecord record : records) {
            long entryMinute = record.getEntryTime().getHour() * 60 + record.getEntryTime().getMinute();
            long exitMinute = record.getExitTime().getHour() * 60 + record.getExitTime().getMinute();
            totalWorkingMinutes += exitMinute - entryMinute;
        }
        Schedule schedule = (Schedule) scheduleRepository.findByEmployeeAndDate( employee,
                LocalDateTime.of(year, month, day, 0,0),
                LocalDateTime.of(year, month, day, 23,59));
        if (schedule == null) {
            return 0.0;
        }
        long scheduleMinutes = Duration.between(schedule.getStartTime(), schedule.getEndTime()).toMinutes();
        if (scheduleMinutes == 0) {
            return 0.0;
        }
        return (double) totalWorkingMinutes / scheduleMinutes * 100;
    }

    @Override
    public long calculateTotalWorkingMinutes(@NotNull List<Schedule> schedules) {
        return schedules.stream()
                .mapToLong(this::calculateWorkingMinutesForSchedule)
                .sum();
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
    public boolean isOnSchedule(@NotNull Employee employee, @NotNull LocalDateTime dateTime) {
        List<Schedule> schedules = scheduleRepository.findByEmployee(employee);
        return schedules.stream()
                .anyMatch(schedule -> isWithinSchedule(schedule, dateTime));
    }

    @Override
    public boolean isWithinSchedule(@NotNull Schedule schedule, @NotNull LocalDateTime dateTime) {
        return !dateTime.isBefore(schedule.getStartTime()) && !dateTime.isAfter(schedule.getEndTime());
    }

    @Override
    public List<AttendanceReportDto> generateAttendanceReport(int year, int month, int day) {
        List<AttendanceRecord> attendanceRecords = attendanceRepository.findAttendanceRecords(year, month, day);

        Map<Employee, List<AttendanceRecord>> recordsByEmployee = attendanceRecords.stream()
                .collect(Collectors.groupingBy(AttendanceRecord::getEmployee));

        List<AttendanceReportDto> report = new ArrayList<>();

        for (Map.Entry<Employee, List<AttendanceRecord>> entry : recordsByEmployee.entrySet()) {
            Employee employee = entry.getKey();
            List<AttendanceRecord> records = entry.getValue();

            long totalWorkingMinutes = records.stream()
                    .mapToLong(record -> Duration.between(record.getEntryTime(), record.getExitTime()).toMinutes())
                    .sum();

            Schedule schedule = (Schedule) scheduleRepository.findByEmployeeAndDate(employee,
                    LocalDateTime.of(year, month, day, 0, 0),
                    LocalDateTime.of(year, month, day, 23, 59));
            if (schedule == null) {
                continue;
            }

                long scheduleMinutes = Duration.between(schedule.getStartTime(), schedule.getEndTime()).toMinutes();

            double attendancePercentage = (double) totalWorkingMinutes / scheduleMinutes * 100;

            AttendanceReportDto dto = new AttendanceReportDto();
            dto.setEmployeeName(employee.getName());
            dto.setDate(LocalDate.of(year, month, day));
            dto.setEntryTime(records.get(0).getEntryTime());
            dto.setExitTime(records.get(records.size() - 1).getExitTime());
            dto.setAttendancePercentage(attendancePercentage);

            report.add(dto);
        }

        return report;
    }

    }

