package com.employed.bar.ports.in;

import com.employed.bar.domain.model.Employee;

import org.springframework.stereotype.Service;


@Service
public interface InputAttendanceCalculationService {

    double calculateAttendancePercentage(Employee employee, int year, int month, int day);
}
