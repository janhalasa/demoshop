package com.halasa.demoshop.service.repository.jpa;

import com.halasa.demoshop.service.domain.RevokedToken;
import com.halasa.demoshop.service.repository.RevokedTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Collections;

@Repository
public class RevokedTokenRepositoryJpa extends BasicReadOnlyRepositoryJpa<RevokedToken, String> implements RevokedTokenRepository {

    @Autowired
    public RevokedTokenRepositoryJpa(EntityManager entityManager, EntityManagerFactory entityManagerFactory) {
        super(ProductRepositoryJpa.class, RevokedToken.class, Collections.emptyList(), entityManager, entityManagerFactory);
    }
}
