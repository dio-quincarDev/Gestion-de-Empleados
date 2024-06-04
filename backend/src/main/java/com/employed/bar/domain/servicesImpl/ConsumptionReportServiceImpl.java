package com.employed.bar.domain.servicesImpl;

import com.employed.bar.adapters.dtos.ConsumptionReportDto;
import com.employed.bar.domain.model.Consumption;
import com.employed.bar.ports.in.ConsumptionRepository;
import com.employed.bar.ports.out.ConsumptionReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsumptionReportServiceImpl implements ConsumptionReportService {
    private final ConsumptionRepository consumptionRepository;

    @Autowired
    public ConsumptionReportServiceImpl(ConsumptionRepository consumptionRepository) {
        this.consumptionRepository = consumptionRepository;
    }
    @Override
    public List<ConsumptionReportDto>generateConsumptionReport() {
        List<Consumption> consumptions = consumptionRepository.findAll();
        LocalDateTime startDate = LocalDateTime.now().minusMonths(1); // Ejemplo de fechas, ajústalas según tus necesidades
        LocalDateTime endDate = LocalDateTime.now();

        return List.of(new ConsumptionReportDto(consumptions, startDate, endDate));

    }
}
