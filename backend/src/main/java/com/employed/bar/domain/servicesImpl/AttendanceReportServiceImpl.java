package com.employed.bar.domain.servicesImpl;


import com.employed.bar.adapters.dtos.AttendanceReportDto;
import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.model.Schedule;
import com.employed.bar.domain.services.AttendanceCalculationService;
import com.employed.bar.ports.in.AttendanceRepository;
import com.employed.bar.ports.in.EmployeeRepository;
import com.employed.bar.ports.in.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceReportServiceImpl implements AttendanceCalculationService {
    private final EmployeeRepository employeeRepository;
    private final AttendanceCalculationService attendanceCalculationService;
    private final ScheduleRepository scheduleRepository;
    private final AttendanceRepository attendanceRepository;

    @Autowired
    public AttendanceReportServiceImpl(EmployeeRepository employeeRepository, AttendanceCalculationService attendanceCalculationService, ScheduleRepository scheduleRepository, AttendanceRepository attendanceRepository) {
        this.employeeRepository = employeeRepository;
        this.attendanceCalculationService = attendanceCalculationService;
        this.scheduleRepository = scheduleRepository;
        this.attendanceRepository = attendanceRepository;
    }
    @Override
    public List<AttendanceReportDto>generateAttendanceReport(int year, int month, int day){
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .map(employee -> createReportForEmployee(employee, year, month, day))
                .collect(Collectors.toList());

    }
    private AttendanceReportDto createReportForEmployee(Employee employee, int year, int month, int day){
        List<AttendanceRecord> attendanceRecords = attendanceRepository.findAttendanceRecordsByEmployeeAndDate(employee, year, month, day);
        AttendanceReportDto report = new AttendanceReportDto();
        report.setEmployeeName(employee.getName());
        report.setAttendanceRecords(attendanceRecords);

        double attendancePercentage = attendanceCalculationService.calculateAttendancePercentage(employee, year, month, day);
               report.setAttendancePercentage(attendancePercentage);
        //Asumiendo que agregamos el porcentaje de asistencia al DTO

        if (!attendanceRecords.isEmpty()) {
            AttendanceRecord firstRecord = attendanceRecords.get(0);
            report.setDate(firstRecord.getDate());
            report.setEntryTime(firstRecord.getEntryTime());
            report.setExitTime(firstRecord.getExitTime());
        }
        return report;
    }

    @Override
    public long calculateTotalWorkingMinutes(List<Schedule> schedules) {
        return schedules.stream()
                .mapToLong(schedule -> Duration.between(schedule.getStartTime(), schedule.getEndTime()).toMinutes())
                .sum();
    }

    @Override
    public boolean isOnSchedule(Employee employee, LocalDateTime dateTime) {
        List<Schedule> schedules = scheduleRepository.findByEmployee(employee);
        return schedules.stream()
                .anyMatch(schedule -> ! schedule.getStartTime()
                        .isAfter(dateTime) && schedule.getEndTime().isBefore(dateTime));

    }

    @Override
    public boolean isWithinSchedule(Schedule schedule, LocalDateTime dateTime) {
        return ! schedule.getStartTime().isAfter(dateTime) && schedule.getEndTime().isBefore(dateTime);
    }

    @Override
    public double calculateAttendancePercentage(Employee employee, int year, int month, int day) {
        List<AttendanceRecord> records = attendanceRepository.findAttendanceRecordsByEmployeeAndDate(employee, year, month, day);
        long totalMinutes = records.stream()
                .mapToLong(record -> Duration.between(record.getEntryTime(), record.getExitTime()).toMinutes()
                ).sum();
        List<Schedule>schedules = scheduleRepository.findByEmployee(employee);
        long totalScheduleMinutes = calculateTotalWorkingMinutes(schedules);

        if (totalScheduleMinutes == 0) {
            return 0.0;
        }
        return (double) totalMinutes / totalScheduleMinutes * 100;
    }
}
