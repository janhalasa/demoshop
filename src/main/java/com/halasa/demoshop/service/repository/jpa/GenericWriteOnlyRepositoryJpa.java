package com.halasa.demoshop.service.repository.jpa;

import com.halasa.demoshop.service.repository.GenericWriteOnlyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@Transactional
public class GenericWriteOnlyRepositoryJpa implements GenericWriteOnlyRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericWriteOnlyRepositoryJpa.class);

    private final EntityManager em;

    @Autowired
    public GenericWriteOnlyRepositoryJpa(EntityManager em) {
        this.em = em;
    }

    @Override
    public <T> void persist(T entity) {
        LOGGER.info("Creating entity {}", entity);
        this.em.persist(entity);
    }

    @Override
    public <T> T merge(T entity) {
        LOGGER.info("Updating entity {}", entity);
        return this.em.merge(entity);
    }

    @Override
    public <T> T save(T entity) {
        return this.merge(entity);
    }

    @Override
    public <T> void remove(T entity) {
        LOGGER.info("Deleting entity {}", entity);
        this.em.remove(entity);
    }

    @Override
    public <T, K> void removeByPk(Class<T> entityClass, K pk) {
        T entity = this.em.find(entityClass, pk);
        if (entity != null) {
            this.em.remove(entity);
        }
    }

    @Override
    public <T> void removeAll(Class<T> entityClass) {
        final String entityName = JpaUtils.getEntityName(entityClass);

        // We cannot use a DELETE query here, because it breaks the Hibernate Search index.
        // If this method is used in production, it would be good to make it smarter - run batch DELETE on non-indexed entities
        // or recreate the index after batch deleting all entities.

        // this.em.createQuery("DELETE FROM " + entityName).executeUpdate();
        List<T> resultList = this.em.createQuery("SELECT t FROM " + entityName + " t").getResultList();
        resultList.forEach(t -> this.em.remove(t));
    }
}
