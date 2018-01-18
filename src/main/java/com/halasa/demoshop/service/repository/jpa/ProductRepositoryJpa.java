package com.halasa.demoshop.service.repository.jpa;

import com.halasa.demoshop.service.domain.Product;
import com.halasa.demoshop.service.domain.Product_;
import com.halasa.demoshop.service.repository.ListResult;
import com.halasa.demoshop.service.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepositoryJpa extends BasicReadOnlyRepositoryJpa<Product, Long> implements ProductRepository {

    @Autowired
    public ProductRepositoryJpa(EntityManager entityManager, EntityManagerFactory entityManagerFactory) {
        super(ProductRepositoryJpa.class, Product.class, Arrays.asList(Product_.picture.getName()), entityManager, entityManagerFactory);
    }

    @Override
    @Transactional
    public ListResult<Product> fulltextSearch(String searchTerm, Optional<Integer> limit, Optional<Integer> offset, List<String> fetches) {
        return this.fulltextSearch(limit, offset, fetches, queryBuilder -> queryBuilder
                .keyword()
                .fuzzy()
                .withEditDistanceUpTo(1)
                .withPrefixLength(1)
                .onFields(Product_.name.getName(), Product_.description.getName())
                .matching(searchTerm)
                .createQuery());
    }
}
