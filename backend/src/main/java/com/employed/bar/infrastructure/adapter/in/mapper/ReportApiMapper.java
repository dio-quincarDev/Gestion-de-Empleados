package com.employed.bar.infrastructure.adapter.in.mapper;

import com.employed.bar.domain.model.report.AttendanceReportLine;
import com.employed.bar.domain.model.report.ConsumptionReportLine;
import com.employed.bar.domain.model.report.Report;
import com.employed.bar.infrastructure.dto.report.AttendanceReportDto;
import com.employed.bar.infrastructure.dto.report.ConsumptionReportDto;
import com.employed.bar.infrastructure.dto.report.ReportDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReportApiMapper {

    @Mapping(source = "attendanceLines", target = "attendanceReports")
    @Mapping(source = "consumptionLines", target = "individualConsumptionReports")
    ReportDto toDto(Report report);

    List<AttendanceReportDto> toAttendanceDtoList(List<AttendanceReportLine> attendanceReportLines);

    List<ConsumptionReportDto> toConsumptionDtoList(List<ConsumptionReportLine> consumptionReportLines);

    @Mapping(target = "attendanceDate", expression = "java(attendanceReportLine.getEntryDateTime().toLocalDate())")
    @Mapping(target = "entryTime", expression = "java(attendanceReportLine.getEntryDateTime().toLocalTime())")
    @Mapping(target = "exitTime", expression = "java(attendanceReportLine.getExitDateTime().toLocalTime())")
    AttendanceReportDto toDto(AttendanceReportLine attendanceReportLine);

    ConsumptionReportDto toDto(ConsumptionReportLine consumptionReportLine);
}
