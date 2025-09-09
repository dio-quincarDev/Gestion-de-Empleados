package com.employed.bar.infrastructure.adapter.out.persistence.adapters;

import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.EmployeeClass;
import com.employed.bar.domain.port.out.AttendanceRepositoryPort;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.AttendanceRecordEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.mapper.AttendanceMapper;
import com.employed.bar.infrastructure.adapter.out.persistence.mapper.EmployeeMapper;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.SpringAttendanceJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AttendancePersistenceAdapter implements AttendanceRepositoryPort {

    private final SpringAttendanceJpaRepository springAttendanceJpaRepository;
    private final AttendanceMapper attendanceMapper;
    private final EmployeeMapper employeeMapper;

    public AttendancePersistenceAdapter(SpringAttendanceJpaRepository springAttendanceJpaRepository, AttendanceMapper attendanceMapper, EmployeeMapper employeeMapper) {
        this.springAttendanceJpaRepository = springAttendanceJpaRepository;
        this.attendanceMapper = attendanceMapper;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public AttendanceRecord save(AttendanceRecord attendanceRecord) {
        AttendanceRecordEntity attendanceRecordEntity = attendanceMapper.toEntity(attendanceRecord);
        AttendanceRecordEntity savedEntity = springAttendanceJpaRepository.save(attendanceRecordEntity);
        return attendanceMapper.toDomain(savedEntity);
    }

    @Override
    public List<AttendanceRecord> findByEmployeeAndDateRange(EmployeeClass employee, LocalDate startDate, LocalDate endDate) {
        return springAttendanceJpaRepository.findByEmployeeAndDateBetween(employeeMapper.toEntity(employee), startDate, endDate).stream()
                .map(attendanceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceRecord> findByEmployee(EmployeeClass employee) {
        return springAttendanceJpaRepository.findByEmployee(employeeMapper.toEntity(employee)).stream()
                .map(attendanceMapper::toDomain)
                .collect(Collectors.toList());
    }
}
