package com.halasa.demoshop.service.repository.jpa.builder;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Functional interface for JPA Criteria Query lambdas.
 */
public interface QueryBuilder<T> {

    CriteriaQuery<T> build(CriteriaBuilder criteriaBuilder, Root<T> root, CriteriaQuery<T> criteriaQuery);
}
