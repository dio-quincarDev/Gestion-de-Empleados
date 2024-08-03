package com.employed.bar.adapters.controllers;

import com.employed.bar.adapters.dtos.ScheduleDto;
import com.employed.bar.application.ScheduleApplicationService;
import com.employed.bar.domain.model.Schedule;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {
    private final ScheduleApplicationService scheduleApplicationService;

    public ScheduleController(ScheduleApplicationService scheduleApplicationService) {
        this.scheduleApplicationService = scheduleApplicationService;
    }

    @PostMapping("/")
    public ResponseEntity<Schedule> createSchedule(@RequestBody ScheduleDto scheduleDto) {
        Schedule createdSchedule = scheduleApplicationService.createSchedule(scheduleDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSchedule);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Schedule>getSchedule(@PathVariable Long id) {
        Schedule schedule = scheduleApplicationService.getScheduleById(id);
        return ResponseEntity.ok(schedule);
    }
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Schedule>>getSchedules(@PathVariable Long employeeId) {
        List<Schedule>schedules = scheduleApplicationService.getSchedulesByEmployee(employeeId);
        return ResponseEntity.ok(schedules);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Schedule> updateSchedule(@PathVariable Long id, @RequestBody Schedule schedule) {
        Schedule updatedSchedule = scheduleApplicationService.updateSchedule(id, schedule);
        return ResponseEntity.ok(updatedSchedule);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleApplicationService.deleteSchedule(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
