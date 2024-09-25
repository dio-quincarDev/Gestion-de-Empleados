package com.employed.bar.adapters.controllers;

import com.employed.bar.adapters.dtos.AttendanceDto;
import com.employed.bar.adapters.dtos.AttendanceReportDto;
import com.employed.bar.application.AttendanceApplicationService;
import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.ports.in.EmployeeRepository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/attendances")
public class AttendanceController {
    private final AttendanceApplicationService attendanceApplicationService;
    private final EmployeeRepository employeeRepository;

    public AttendanceController(AttendanceApplicationService attendanceApplicationService, EmployeeRepository employeeRepository) {
        this.attendanceApplicationService = attendanceApplicationService;
        this.employeeRepository = employeeRepository;
    }

    @PostMapping("/")
    public ResponseEntity<?> registerAttendance(@RequestBody AttendanceDto attendanceDto) {
        try {
            if (attendanceDto.getEmployeeId() == null) {
                return ResponseEntity.badRequest().body("Employee ID is required");
            }
            AttendanceRecord attendanceRecord = attendanceApplicationService.registerAttendance(attendanceDto);
            return ResponseEntity.ok(attendanceRecord);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/report")
    public ResponseEntity <List<AttendanceReportDto>>generateAttendanceReport(@RequestParam Long employeeId,
                                                                              @RequestParam int year,
                                                                              @RequestParam int month,
                                                                              @RequestParam int day){
        LocalDate date = LocalDate.of(year, month, day);
        LocalDateTime startDate = LocalDateTime.of(year, month, day, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(year, month, day, 23, 59);

        List<AttendanceReportDto> report = attendanceApplicationService.generateAttendanceReport(year, month, day, employeeId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/percentage")
    public ResponseEntity<Double>calculateAttendancePercentage(@RequestParam Long employeeId,
                                                               @RequestParam int year,
                                                               @RequestParam int month,
                                                               @RequestParam int day){
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(()-> new IllegalArgumentException("Employee Not Found"));
        double percentage = attendanceApplicationService.calculateAttendancePercentage(employee, year, month, day);
        return ResponseEntity.ok(percentage);
    }

}
