package com.employed.bar.infrastructure.adapter.out.persistence.entity;

import com.employed.bar.domain.enums.AttendanceStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table (name = "attendance_records")
public class AttendanceRecordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    @JsonBackReference
    private EmployeeEntity employee;

    @Column(name = "entry_date_time")
    private LocalDateTime entryDateTime;

    @Column(name = "exit_date_time")
    private LocalDateTime exitDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status" , nullable = false)
    private AttendanceStatus status;


}
