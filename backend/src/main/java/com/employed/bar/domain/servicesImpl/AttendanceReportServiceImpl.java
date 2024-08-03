package com.employed.bar.domain.servicesImpl;

import com.employed.bar.adapters.dtos.AttendanceReportDto;
import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.services.AttendanceReportService;
import com.employed.bar.domain.services.AttendanceService;
import com.employed.bar.ports.out.AttendanceRepository;
import com.employed.bar.ports.in.EmployeeRepository;
import com.employed.bar.ports.in.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceReportServiceImpl implements AttendanceReportService {
    private final EmployeeRepository employeeRepository;
    private final ScheduleRepository scheduleRepository;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceService attendanceService;

    @Autowired
    public AttendanceReportServiceImpl(EmployeeRepository employeeRepository, ScheduleRepository scheduleRepository,
                                       AttendanceRepository attendanceRepository, AttendanceService attendanceService) {
        this.employeeRepository = employeeRepository;
        this.scheduleRepository = scheduleRepository;
        this.attendanceRepository = attendanceRepository;
        this.attendanceService = attendanceService;
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

        double attendancePercentage = attendanceService.calculateAttendancePercentage(employee, year, month, day);
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

}
