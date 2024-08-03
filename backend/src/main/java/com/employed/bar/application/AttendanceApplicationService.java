package com.employed.bar.application;

import com.employed.bar.adapters.dtos.AttendanceDto;
import com.employed.bar.adapters.dtos.AttendanceReportDto;
import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.services.AttendanceService;
import com.employed.bar.ports.in.EmployeeRepository;
import com.employed.bar.ports.out.AttendanceRepository;
import com.employed.bar.domain.services.AttendanceReportService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceApplicationService  {
    private final EmployeeRepository employeeRepository;
    private final AttendanceReportService AttendanceReportService;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceService attendanceService;


    public AttendanceApplicationService(EmployeeRepository employeeRepository,
                                        AttendanceReportService AttendanceReportService, AttendanceRepository attendanceRepository, AttendanceService attendanceService) {
        this.employeeRepository = employeeRepository;
        this.AttendanceReportService = AttendanceReportService;
        this.attendanceRepository = attendanceRepository;
        this.attendanceService = attendanceService;
    }
    @Transactional
    public AttendanceRecord registerAttendance(AttendanceDto attendanceDto) {
       Long employeeId = attendanceDto.getEmployeeId();

       if (employeeId == null){
           throw new IllegalArgumentException("Employee ID cannot be null");
       }
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
        return attendanceService.calculateAttendancePercentage(employee, year, month, day);
    }
}
