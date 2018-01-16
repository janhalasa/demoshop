package com.halasa.demoshop.service.repository.jpa.builder;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Functional interface for JPA Criteria Query lambdas.
 */
public interface PredicateBuilder<T> {

    Predicate build(CriteriaBuilder criteriaBuilder, Root<T> root);
}
