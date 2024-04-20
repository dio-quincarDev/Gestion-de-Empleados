package com.employed.bar.adapters.controllers;

import com.employed.bar.adapters.dtos.ConsumptionDto;
import com.employed.bar.application.ConsumptionApplicationService;
import com.employed.bar.domain.exceptions.InvalidConsumptionDataException;
import com.employed.bar.domain.model.Consumption;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/consumptions")
public class ConsumptionController {
    private final ConsumptionApplicationService consumptionApplicationService;
    public ConsumptionController(ConsumptionApplicationService consumptionApplicationService) {
        this.consumptionApplicationService = consumptionApplicationService;
    }
    @PostMapping
    public ResponseEntity<Consumption>calculateConsumption(@RequestBody @Valid ConsumptionDto consumptionDto){
        try{
            Consumption consumption = consumptionApplicationService.processConsumption(consumptionDto);
            return ResponseEntity.ok(consumption);
        }catch(InvalidConsumptionDataException e){
            return ResponseEntity.badRequest().body(null);
        }
    }
    @ResponseStatus
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
}
