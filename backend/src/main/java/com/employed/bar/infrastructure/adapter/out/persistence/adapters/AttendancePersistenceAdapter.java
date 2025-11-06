package com.employed.bar.infrastructure.adapter.out.persistence.adapters;

import com.employed.bar.domain.model.structure.AttendanceRecordClass;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.port.out.AttendanceRepositoryPort;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.AttendanceRecordEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.mapper.AttendanceMapper;
import com.employed.bar.infrastructure.adapter.out.persistence.mapper.EmployeeMapper;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.SpringAttendanceJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
    public AttendanceRecordClass save(AttendanceRecordClass attendanceRecordClass) {
        AttendanceRecordEntity attendanceRecordEntity = attendanceMapper.toEntity(attendanceRecordClass);
        AttendanceRecordEntity savedEntity = springAttendanceJpaRepository.save(attendanceRecordEntity);
        return attendanceMapper.toDomain(savedEntity);
    }

    @Override
    public List<AttendanceRecordClass> findByEmployeeAndDateRange(EmployeeClass employee, LocalDateTime startDate, LocalDateTime endDate) {
                                return springAttendanceJpaRepository.findByEmployeeAndEntryDateTimeBetween(
                                        employee.getId(), startDate, endDate).stream()
                .map(attendanceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceRecordClass> findByEmployee(EmployeeClass employee) {
        return springAttendanceJpaRepository.findByEmployee(employeeMapper.toEntity(employee)).stream()
                .map(attendanceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AttendanceRecordClass> findTopByEmployeeOrderByEntryDateTimeDesc(EmployeeClass employee) {
        return springAttendanceJpaRepository.findTopByEmployeeOrderByEntryDateTimeDesc(employeeMapper.toEntity(employee))
                .map(attendanceMapper::toDomain);
    }

    @Override
    public Optional<AttendanceRecordClass> findById(Long attendanceId) {
        return springAttendanceJpaRepository.findById(attendanceId)
                .map(attendanceMapper::toDomain);
    }

    @Override
    public void deleteById(Long attendanceId) {
        springAttendanceJpaRepository.deleteById(attendanceId);
    }
}
