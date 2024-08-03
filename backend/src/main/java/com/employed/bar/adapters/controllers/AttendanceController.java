package com.employed.bar.adapters.controllers;

import com.employed.bar.adapters.dtos.AttendanceDto;
import com.employed.bar.adapters.dtos.AttendanceReportDto;
import com.employed.bar.application.AttendanceApplicationService;
import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.ports.in.EmployeeRepository;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<AttendanceRecord>registerAttendance(@RequestBody AttendanceDto attendanceDto){
        if (attendanceDto.getEmployeeId() == null) {
            throw new IllegalArgumentException("Employee ID is required");
        }
       AttendanceRecord attendanceRecord = attendanceApplicationService.registerAttendance(attendanceDto);
        return ResponseEntity.ok(attendanceRecord);
    }
    @GetMapping("/report")
    public ResponseEntity <List<AttendanceReportDto>>generateAttendanceReport(@RequestParam int year,
                                                                              @RequestParam int month,
                                                                              @RequestParam int day){
        List<AttendanceReportDto> report = attendanceApplicationService.generateAttendanceReport(year, month, day);
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
