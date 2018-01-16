package com.halasa.demoshop.service.repository.jpa;

import com.halasa.demoshop.service.repository.jpa.builder.PredicateBuilder;
import com.halasa.demoshop.service.repository.jpa.builder.QueryBuilder;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BasicRepositoryJpa<T> {

    private final EntityManager em;
    private final Class<T> entityClass;
    private final String entityName;
    private final String pkFieldName;
    private final Logger logger;
    private final T entityObject;

    protected BasicRepositoryJpa(Class<? extends BasicRepositoryJpa> repositoryClass, Class<T> entityClass, EntityManager em,
            EntityManagerFactory entityManagerFactory) {
        this.em = em;
        this.entityClass = entityClass;
        this.entityName = JpaUtils.getEntityName(entityClass);
        this.logger = LoggerFactory.getLogger(repositoryClass.getName());

        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        this.pkFieldName = sessionFactory.getClassMetadata(entityClass).getIdentifierPropertyName();

        try {
            this.entityObject = entityClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IllegalArgumentException("Error creating an entity object of class " + entityClass.getSimpleName(), ex);
        }
    }

    protected Logger logger() {
        return this.logger;
    }

    protected EntityManager em() {
        return this.em;
    }

    protected Class<T> entityClass() {
        return this.entityClass;
    }

    protected String entityName() {
        return this.entityName;
    }

    protected String pkFieldName() {
        return this.pkFieldName;
    }

    protected T entityObject() {
        return this.entityObject;
    }

    /**
     * Creates a TypedQuery which can be further customized by calling its methods such as setMaxResults() or setFirstResult.
     * To get results, call its getResultList() or getSingleResult() method.
     * Method is private, so it cannot be overridden - it's used by other methods.
     */
    private TypedQuery<T> createTypedQuery(QueryBuilder<T> queryBuilder) {
        CriteriaBuilder cb = em().getCriteriaBuilder();
        CriteriaQuery<T> q = cb.createQuery(entityClass);
        Root<T> root = q.from(entityClass);
        ParameterExpression<Integer> p = cb.parameter(Integer.class);
        CriteriaQuery<T> criteriaQuery = q.select(root);
        criteriaQuery = queryBuilder.build(cb, root, criteriaQuery);
        TypedQuery<T> typedQuery = em.createQuery(criteriaQuery);
        return typedQuery;
    }

    protected TypedQuery<T> createQuery(QueryBuilder<T> queryBuilder) {
        return createTypedQuery(queryBuilder);
    }

    public List<T> find(QueryBuilder<T> queryBuilder) {
        return createQuery(queryBuilder).getResultList();
    }

    /**
     * Finds all entities matching the predicate.
     *
     * @param predicateBuilders Restricting query conditions. If you supply more than one predicate, they will be joined by conjunction.
     */
    protected List<T> findWhere(PredicateBuilder<T>... predicateBuilders) {
        return createTypedQuery(
                (cb, root, query) -> (query.where(buildPredicates(cb, root, predicateBuilders))))
                .getResultList();
    }

    /**
     * Finds a single entity matching the predicate.
     *
     * @param predicateBuilders Restricting query conditions. If you supply more than one predicate, they will be joined by conjunction.
     */
    protected T loadWhere(PredicateBuilder<T>... predicateBuilders) {
        return createTypedQuery(
                (cb, root, query) -> (query.where(buildPredicates(cb, root, predicateBuilders))))
                .getSingleResult();
    }

    protected Optional<T> getWhere(PredicateBuilder<T>... predicateBuilders) {
        List<T> results = createTypedQuery(
                (cb, root, query) -> (query.where(buildPredicates(cb, root, predicateBuilders))))
                .getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        }
        if (results.size() > 1) {
            throw new NonUniqueResultException("There were " + results.size() + " results");
        }
        return Optional.of(results.get(0));
    }

    protected Predicate[] buildPredicates(CriteriaBuilder cb, Root<T> root, PredicateBuilder<T>... predicateBuilders) {
        List<Predicate> predicates = new ArrayList<>();
        if (predicateBuilders != null && predicateBuilders.length > 0) {
            for (PredicateBuilder<T> builder : predicateBuilders) {
                predicates.add(builder.build(cb, root));
            }
        }
        return predicates.toArray(new Predicate[predicates.size()]);
    }
}
