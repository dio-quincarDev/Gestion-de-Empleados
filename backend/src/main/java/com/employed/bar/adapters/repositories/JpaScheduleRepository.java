package com.employed.bar.adapters.repositories;

import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.model.Schedule;
import com.employed.bar.ports.in.ScheduleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class JpaScheduleRepository implements ScheduleRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Schedule save(Schedule schedule){
        entityManager.persist(schedule);
        return schedule;
    }

    @Override
    public Schedule findById(Long id){
        return entityManager.find(Schedule.class, id);
    }

    @Override
    @Query("SELECT s FROM Schedule s")
    public List<Schedule> findAll(){
        return  entityManager.createQuery("SELECT s FROM Schedule s", Schedule.class).getResultList();
    }
    @Override
    @Modifying
    public void deleteById(Long id){
        Schedule schedule = findById(id);
        if (schedule != null){
            entityManager.remove(schedule);
        }
    }

    @Override
    public List<Schedule> findByEmployee(Employee employee) {
        return entityManager.createQuery("SELECT s FROM Schedule s WHERE s.employee= :employee", Schedule.class)
                .setParameter("employee", employee)
                .getResultList();
    }
}
