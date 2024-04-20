package com.employed.bar.ports.in;

import com.employed.bar.domain.model.Employee;
import org.springframework.stereotype.Service;

@Service
public interface AttendanceCalculationService {
    double calculateAttendancePercentege(Employee employee, int year, int month, int day);

    double calculateAttendancePercentage(Employee employee, int year, int month, int day);
}
