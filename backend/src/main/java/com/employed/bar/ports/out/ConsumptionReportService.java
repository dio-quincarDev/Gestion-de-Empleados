package com.employed.bar.ports.out;

import com.employed.bar.adapters.dtos.ConsumptionReportDto;

import java.util.List;

public interface ConsumptionReportService {
    List<ConsumptionReportDto> generateConsumptionReport();
}
