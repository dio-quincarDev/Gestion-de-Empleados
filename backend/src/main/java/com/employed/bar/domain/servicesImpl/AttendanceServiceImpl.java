package com.employed.bar.domain.servicesImpl;

import com.employed.bar.adapters.dtos.AttendanceDto;
import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.services.AttendanceService;
import com.employed.bar.ports.in.EmployeeRepository;
import com.employed.bar.ports.in.InputAttendanceCalculationService;
import com.employed.bar.domain.exceptions.InvalidAttendanceDataException;
import com.employed.bar.ports.in.AttendanceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    private final EmployeeRepository employeeRepository;
    private final InputAttendanceCalculationService attendanceCalculationService;
    private final AttendanceRepository attendanceRepository;


    public AttendanceServiceImpl(EmployeeRepository employeeRepository, InputAttendanceCalculationService attendanceCalculationService, InputAttendanceCalculationService inputAttendanceCalculationService, AttendanceRepository attendanceRepository, AttendanceRepository attendanceRepository1) {
        this.employeeRepository = employeeRepository;
        this.attendanceCalculationService = attendanceCalculationService;
        this.attendanceRepository = attendanceRepository1;
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
        return attendanceRepository.findAttendanceRecordsByEmployeeAndDate(employee, date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    @Override
    public double calculateAtendancePercentege(Employee employee, int year, int month, int day) {
        return attendanceCalculationService.calculateAttendancePercentage(employee, year, month, day);
    }


    }

