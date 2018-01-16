package com.halasa.demoshop.service.repository.jpa;

import com.github.tennaito.rsql.jpa.JpaCriteriaQueryVisitor;
import com.halasa.demoshop.service.OrderBy;
import com.halasa.demoshop.service.OrderByDirection;
import com.halasa.demoshop.service.repository.BasicReadOnlyRepository;
import com.halasa.demoshop.service.repository.ListResult;
import com.halasa.demoshop.service.validation.UnsupportedAssociationFetchException;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class BasicReadOnlyRepositoryJpa<T, K extends Serializable>
        extends BasicRepositoryJpa<T>
        implements BasicReadOnlyRepository<T, K> {

    private static final String JAVAX_PERSISTENCE_LOADGRAPH = "javax.persistence.loadgraph";

    private Set<String> supportedFetchValues;

    public BasicReadOnlyRepositoryJpa(
            Class<? extends BasicRepositoryJpa> repositoryClass,
            Class<T> entityClass,
            List<String> supportedFetchValues,
            EntityManager entityManager,
            EntityManagerFactory entityManagerFactory) {
        super(repositoryClass, entityClass, entityManager, entityManagerFactory);
        this.supportedFetchValues = new HashSet<>(supportedFetchValues);
    }

    @Transactional
    @Override
    public T getByPk(K pk) {
        logger().debug("Getting entity {} with ID {}", entityClass().getSimpleName(), pk);
        T result = this.em().find(this.entityClass(), pk);
        if (result == null) {
            throw new NoResultException("There is no entity of type " + entityClass().getSimpleName() + " with PK " + pk);
        }
        return result;
    }

    @Transactional
    @Override
    public T getByPk(K pk, List<String> fetches) {
        final CriteriaBuilder cb = this.em().getCriteriaBuilder();
        final CriteriaQuery<T> query = cb.createQuery(entityClass());
        final Root<T> root = query.from(entityClass());
        query.where(cb.equal(root.get(pkFieldName()), pk));
        final TypedQuery<T> typedQuery = this.em().createQuery(query);
        this.applyFetches(typedQuery, fetches);

        return typedQuery.getSingleResult();
    }

    @Transactional
    @Override
    public ListResult<T> search(
            Optional<String> rsqlQuery,
            Optional<Integer> limit,
            Optional<Integer> offset,
            List<String> fetches,
            List<OrderBy> orderByList) {

        CriteriaQuery<T> query = null;
        Root<T> root;
        CriteriaBuilder cb = this.em().getCriteriaBuilder();

        if (rsqlQuery.isPresent()) {
            Set<ComparisonOperator> operators = RSQLOperators.defaultOperators();
            operators.add(new ComparisonOperator("=like=", true));

            Node rootNode = new RSQLParser(operators).parse(rsqlQuery.get());

            JpaCriteriaQueryVisitor<T> visitor = new JpaCriteriaQueryVisitor<T>(this.entityObject());

            query = rootNode.accept(visitor, this.em());
            root = (Root<T>) query.getRoots().stream().findFirst().get();
        } else {
            query = cb.createQuery(entityClass());
            root = query.from(entityClass());
            query.select(root);
        }

        for (OrderBy orderBy: orderByList) {
            final Path<Object> fieldPath = root.get(orderBy.getFieldName());
            if (orderBy.getDirection() == OrderByDirection.ASC) {
                query.orderBy(cb.asc(fieldPath));
            } else {
                query.orderBy(cb.desc(fieldPath));
            }
        }

        final TypedQuery<T> typedQuery = this.em().createQuery(query);

        this.applyFetches(typedQuery, fetches);

        if (limit.isPresent()) {
            typedQuery.setMaxResults(limit.get());
        }
        if (offset.isPresent()) {
            typedQuery.setFirstResult(offset.get());
        }

        List<T> results = typedQuery.getResultList();

        final Long totalCount = JpaUtils.count(em(), query);

        return new ListResult<T>(
                results,
                Optional.of(totalCount),
                limit,
                offset
        );
    }

    private void applyFetches(TypedQuery<T> typedQuery, List<String> fetches) {
        if (fetches == null) {
            return;
        }

        fetches.forEach(fetchValue -> {
            if (! this.supportedFetchValues.contains(fetchValue)) {
                throw new UnsupportedAssociationFetchException("Invald fetch value: " + fetchValue);
            }
        });

        EntityGraph<T> graph = this.em().createEntityGraph(entityClass());
        for (String associationNameToFetch : fetches) {
            JpaUtils.fetchesToEntityGraph(graph, associationNameToFetch);
        }

        if (!graph.getAttributeNodes().isEmpty()) {
            typedQuery.setHint(JAVAX_PERSISTENCE_LOADGRAPH, graph);
        }
    }
}
