package com.employed.bar.adapters.jpaRepositories;

import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.model.Schedule;
import com.employed.bar.ports.in.ScheduleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public interface JpaScheduleRepository extends ScheduleRepository {

        @Override
        @Query("SELECT s FROM Schedule s WHERE s.employee = :employee")
        List<Schedule> findByEmployee(@Param("employee") Employee employee);

        @Override
        @Query("SELECT s FROM Schedule s WHERE s.employee = :employee AND s.date = :date")
        Schedule findByEmployeeAndDate(@Param("employee") Employee employee, @Param("date") LocalDate date);

}
