package com.employed.bar.adapters.repositories;

import com.employed.bar.domain.model.Consumption;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.ports.in.ConsumptionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Repository
@Transactional
public  class JpaConsumptionRepository implements ConsumptionRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Consumption save(Consumption consumption) {
        entityManager.persist(consumption);
        return consumption;
    }

    @Override
    public Optional<Consumption> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Consumption.class, id));
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public <S extends Consumption> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public List<Consumption> findAll() {
        return entityManager.createQuery("SELECT c FROM Consumption c", Consumption.class).getResultList();
    }

    @Override
    public List<Consumption> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(Consumption consumption) {
        entityManager.remove(consumption);
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Consumption> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Consumption> findByEmployeeAndDateTimeBetween(Employee employee, LocalDateTime startDate, LocalDateTime endDate) {
        return entityManager.createQuery("SELECT c FROM Consumption c WHERE c.employee = :employee AND c.dateTime BETWEEN :startDate AND :endDate", Consumption.class)
                .setParameter("employee", employee)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }



    @Override
    public void flush() {
        entityManager.flush();
    }

    @Override
    public <S extends Consumption> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Consumption> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Consumption> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Consumption getOne(Long aLong) {
        return null;
    }

    @Override
    public Consumption getById(Long aLong) {
        return null;
    }

    @Override
    public Consumption getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Consumption> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Consumption> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Consumption> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Consumption> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Consumption> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Consumption> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Consumption, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<Consumption> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Consumption> findAll(Pageable pageable) {
        return null;
    }
}
