package com.employed.bar.domain.servicesImpl;

import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.model.Schedule;
import com.employed.bar.ports.in.AttendanceCalculationService;
import com.employed.bar.ports.in.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceCalculationServiceImpl implements AttendanceCalculationService {
   private final EmployeeRepository employeeRepository;
   public AttendanceCalculationServiceImpl(EmployeeRepository employeeRepository){
       this.employeeRepository = employeeRepository;
   }
   @Override
    public double calculateAttendancePercentege(Employee employee, int year, int month, int day){
       List<AttendanceRecord> records = employeeRepository.findAttendanceRecords(year, month, day);
       long totalWorkedMinutes = 0;
       for (AttendanceRecord record : records) {
           totalWorkedMinutes += record.getExitTime().getMinute() - record.getEntryTime().getMinute();

       }
    Schedule schedule = employeeRepository.findByEmployeeAndDate(employee, year, month, day); {
           long scheduleMinutes = schedule.getEndTime().getMinute() - schedule.getStartTime().getMinute();
           if (scheduleMinutes ==0){
               return 0.0;
           }
           return(double)totalWorkedMinutes/scheduleMinutes;
       }

   }

    @Override
    public double calculateAttendancePercentage(Employee employee, int year, int month, int day) {
        return 0;
    }
}
