package com.employed.bar.adapters.controllers;

import com.employed.bar.adapters.dtos.ConsumptionDto;
import com.employed.bar.application.ConsumptionApplicationService;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.exceptions.InvalidConsumptionDataException;
import com.employed.bar.domain.model.Consumption;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/consumptions")
public class ConsumptionController {
    private final ConsumptionApplicationService consumptionApplicationService;

    @PostMapping("/")
    public ResponseEntity<Consumption> createConsumption(@RequestBody @Valid ConsumptionDto consumptionDto) {
        Consumption consumption = consumptionApplicationService.processConsumption(consumptionDto);
        return ResponseEntity.ok(consumption);
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String>handleValidationConsumption(MethodArgumentNotValidException ex){
        Map<String, String>errors = new HashMap<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach(error ->{
                    String fieldName = ((FieldError)error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });
        return errors;

    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal>getTotalConsumptionByEmployee(
            @RequestParam Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
        BigDecimal total = consumptionApplicationService.calculateTotalConsumptionByEmployee(employeeId, startDate, endDate);
        return ResponseEntity.ok(total);

    }

    @GetMapping("/total/all")
    public ResponseEntity<BigDecimal>getTotalConsumptionForAllEmployees(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){

        BigDecimal total = consumptionApplicationService.calculateTotalConsumptionForAllEmployees(startDate, endDate);
        return ResponseEntity.ok(total);
    }


    @ExceptionHandler(EmployeeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleEmployeeNotFoundException(EmployeeNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidConsumptionDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleInvalidConsumptionDataException(InvalidConsumptionDataException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }


}
