package com.employed.bar.application;

import com.employed.bar.adapters.dtos.AttendanceDto;
import com.employed.bar.adapters.dtos.AttendanceReportDto;
import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.ports.in.EmployeeRepository;
import com.employed.bar.ports.in.InputAttendanceCalculationService;
import com.employed.bar.ports.in.AttendanceRepository;
import com.employed.bar.ports.out.AttendanceReportService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceApplicationService  {
    private final EmployeeRepository employeeRepository;
    private final InputAttendanceCalculationService attendanceCalculationService;
    private final AttendanceReportService AttendanceReportService;
    private final AttendanceRepository attendanceRepository;


    public AttendanceApplicationService(EmployeeRepository employeeRepository,
                                        InputAttendanceCalculationService attendanceCalculationService,
                                        AttendanceReportService AttendanceReportService, AttendanceRepository attendanceRepository) {
        this.employeeRepository = employeeRepository;
        this.attendanceCalculationService = attendanceCalculationService;
        this.AttendanceReportService = AttendanceReportService;
        this.attendanceRepository = attendanceRepository;
    }
    @Transactional
    public AttendanceRecord registerAttendance(AttendanceDto attendanceDto) {
       Long employeeId = attendanceDto.getEmployeeId();
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        AttendanceRecord attendanceRecord = new AttendanceRecord();
        attendanceRecord.setEmployee(employee);
        attendanceRecord.setDate(attendanceDto.getDate());
        attendanceRecord.setEntryTime(attendanceDto.getEntryTime());
        attendanceRecord.setExitTime(attendanceDto.getExitTime());
        attendanceRecord.setStatus(attendanceDto.getStatus());

        return attendanceRepository.save(attendanceRecord);
    }
    public List<AttendanceReportDto>generateAttendanceReport(int year, int month, int day){
        return AttendanceReportService.generateAttendanceReport(year, month, day);
    }
    public double calculateAttendancePercentage(Employee employee, int year, int month, int day){
        return attendanceCalculationService.calculateAttendancePercentage(employee, year, month, day);
    }
}
