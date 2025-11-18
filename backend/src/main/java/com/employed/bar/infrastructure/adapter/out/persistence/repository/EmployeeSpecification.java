package com.employed.bar.infrastructure.adapter.out.persistence.repository;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeSpecification {

    public static Specification<EmployeeEntity> nameContains(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<EmployeeEntity> hasRole(EmployeeRole role) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("role"), role);
    }

    public static Specification<EmployeeEntity> hasStatus(EmployeeStatus status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<EmployeeEntity> isActiveOrInactive() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.notEqual(root.get("status"), EmployeeStatus.TERMINATED);
    }
}
