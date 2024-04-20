package com.employed.bar.adapters.repositories;

import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.model.Schedule;
import com.employed.bar.ports.in.EmployeeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class JpaEmployeeRepository implements EmployeeRepository{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Employee save(Employee employee){
        entityManager.persist(employee);
        return employee;
    }


    @Override
    public List<AttendanceRecord> findAttendanceRecordsByEmployeeAndDate(Employee employee, int year, int month, int day) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AttendanceRecord> query = criteriaBuilder.createQuery(AttendanceRecord.class);
        Root<AttendanceRecord> ar = query.from(AttendanceRecord.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(ar.get("employee"), employee));
        predicates.add(criteriaBuilder.equal(ar.get("date"), LocalDate.of(year, month, day)));

        query.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public Employee findById(Long id){
        return entityManager.find(Employee.class, id);
    }

    @Override
    public List<Employee> findAll(){
        return entityManager.createQuery("SELECT e FROM Employee e", Employee.class).getResultList();
    }

    @Override
    public void delete(Employee employee){
        entityManager.remove(employee);
    }

    @Override
    public Schedule findByEmployeeAndDate(Employee employee, int year, int month, int day) {
        return null;
    }

    @Override
    public List<AttendanceRecord> findAttendanceRecords(int year, int month, int day) {
        return null;
    }
}
