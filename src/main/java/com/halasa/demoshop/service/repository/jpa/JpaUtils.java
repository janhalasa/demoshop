package com.halasa.demoshop.service.repository.jpa;

import com.halasa.demoshop.service.validation.UnsupportedAssociationFetchException;
import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Subgraph;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.util.List;

/**
 * This class was taken from (just methods needed in this project)
 * https://github.com/chelu/jdal/blob/master/core/src/main/java/org/jdal/dao/jpa/JpaUtils.java
 *
 * @author Jose Luis Martin, Ján Halaša
 */
public class JpaUtils {

    public static final String JAVAX_PERSISTENCE_LOADGRAPH = "javax.persistence.loadgraph";

    private static volatile int aliasCount = 0;

    /**
     * Result count from a CriteriaQuery.
     * @param em Entity Manager
     * @param criteria Criteria Query to count results
     * @return row count
     * @author Jose Luis Martin
     */
    public static <T> Long count(EntityManager em, CriteriaQuery<T> criteria) {
        return em.createQuery(countCriteria(em, criteria)).getSingleResult();
    }

    /**
     * Create a row count CriteriaQuery from a CriteriaQuery.
     * @param em entity manager
     * @param criteria source criteria
     * @return row count CriteriaQuery
     * @author Jose Luis Martin
     */
    public static <T> CriteriaQuery<Long> countCriteria(EntityManager em, CriteriaQuery<T> criteria) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
        copyCriteriaWithoutSelectionAndOrder(criteria, countCriteria, false);

        Expression<Long> countExpression;

        if (criteria.isDistinct()) {
            countExpression = builder.countDistinct(findRoot(countCriteria, criteria.getResultType()));
        } else {
            countExpression = builder.count(findRoot(countCriteria, criteria.getResultType()));
        }

        return countCriteria.select(countExpression);
    }

    /**
     * Copy criteria without selection and order.
     * @param from source Criteria.
     * @param to destination Criteria.
     * @author Jose Luis Martin
     */
    private static void copyCriteriaWithoutSelectionAndOrder(
            CriteriaQuery<?> from, CriteriaQuery<?> to, boolean copyFetches) {
        if (isEclipseLink(from) && from.getRestriction() != null) {
            // EclipseLink adds roots from predicate paths to critera. Skip copying
            // roots as workaround.
        } else {
            // Copy Roots
            for (Root<?> root : from.getRoots()) {
                Root<?> dest = to.from(root.getJavaType());
                dest.alias(getOrCreateAlias(root));
                copyJoins(root, dest);
                if (copyFetches) {
                    copyFetches(root, dest);
                }
            }
        }

        to.groupBy(from.getGroupList());
        to.distinct(from.isDistinct());

        if (from.getGroupRestriction() != null) {
            to.having(from.getGroupRestriction());
        }

        Predicate predicate = from.getRestriction();
        if (predicate != null) {
            to.where(predicate);
        }
    }

    /**
     * Gets The result alias, if none set a default one and return it.
     * @return root alias or generated one
     * @author Jose Luis Martin
     */
    public static synchronized <T> String getOrCreateAlias(Selection<T> selection) {
        // reset alias count
        if (aliasCount > 1000) {
            aliasCount = 0;
        }

        String alias = selection.getAlias();
        if (alias == null) {
            alias = "JDAL_generatedAlias" + aliasCount++;
            selection.alias(alias);
        }
        return alias;

    }

    /**
     * Find the Root with type class on CriteriaQuery Root Set.
     * @param <T> root type
     * @param query criteria query
     * @param clazz root type
     * @return Root&lt;T&gt; of null if none
     * @author Jose Luis Martin
     */
    @SuppressWarnings("unchecked")
    public static  <T> Root<T> findRoot(CriteriaQuery<?> query, Class<T> clazz) {

        for (Root<?> r : query.getRoots()) {
            if (clazz.equals(r.getJavaType())) {
                return (Root<T>) r.as(clazz);
            }
        }
        return null;
    }

    private static boolean isEclipseLink(CriteriaQuery<?> from) {
        return from.getClass().getName().contains("org.eclipse.persistence");
    }

    /**
     * Copy Joins.
     * @param from source Join
     * @param to destination Join
     * @author Jose Luis Martin
     */
    public static void copyJoins(From<?, ?> from, From<?, ?> to) {
        for (Join<?, ?> j : from.getJoins()) {
            Join<?, ?> toJoin = to.join(j.getAttribute().getName(), j.getJoinType());
            toJoin.alias(getOrCreateAlias(j));

            copyJoins(j, toJoin);
        }
    }

    /**
     * Copy Fetches.
     * @param from source From
     * @param to destination From
     * @author Jose Luis Martin
     */
    public static void copyFetches(From<?, ?> from, From<?, ?> to) {
        for (Fetch<?, ?> f : from.getFetches()) {
            Fetch<?, ?> toFetch = to.fetch(f.getAttribute().getName());
            copyFetches(f, toFetch);
        }
    }

    /**
     * Copy Fetches.
     * @param from source Fetch
     * @param to dest Fetch
     * @author Jose Luis Martin
     */
    public static void copyFetches(Fetch<?, ?> from, Fetch<?, ?> to) {
        for (Fetch<?, ?> f : from.getFetches()) {
            Fetch<?, ?> toFetch = to.fetch(f.getAttribute().getName());
            // recursively copy fetches
            copyFetches(f, toFetch);
        }
    }

    /**
     * Resolves entity name of the given class.
     * @return Entity name.
     * @author Ján Halaša
     */
    public static String getEntityName(Class<?> entityClass) {
        final Entity entityAnnotation = entityClass.getAnnotation(Entity.class);
        if (entityAnnotation == null) {
            throw new IllegalArgumentException("Class " + entityClass.getName() + " is not a JPA entity.");
        }
        return StringUtils.hasText(entityAnnotation.name()) ? entityAnnotation.name() : entityClass.getSimpleName();
    }

    public static <T> void fetchesToEntityGraph(EntityGraph<T> graphRoot, String path) {
        final String[] parts = path.split("\\.");
        Subgraph<?> itemGraph = null;
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (i < parts.length - 1) {
                itemGraph = graphRoot.addSubgraph(part);
            } else if (itemGraph != null) {
                itemGraph.addAttributeNodes(part);
            } else {
                graphRoot.addAttributeNodes(part);
            }
        }
    }

}
