package com.employed.bar.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table (name = "attendance_records")
public class AttendanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

   @Column(name = "date")
    private LocalDate date;

   @Column(name = "entry_time")
    private LocalTime entryTime;

   @Column(name = "exit_time")
    private LocalTime exitTime;

    @Column(name = "status")
    private String status;


}
