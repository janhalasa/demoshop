package com.halasa.demoshop.service.repository.jpa;

import com.halasa.demoshop.service.domain.Product;
import com.halasa.demoshop.service.domain.Product_;
import com.halasa.demoshop.service.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Arrays;

@Repository
public class ProductRepositoryJpa extends BasicReadOnlyRepositoryJpa<Product, Long> implements ProductRepository {

    @Autowired
    public ProductRepositoryJpa(EntityManager entityManager, EntityManagerFactory entityManagerFactory) {
        super(ProductRepositoryJpa.class, Product.class, Arrays.asList(Product_.picture.getName()), entityManager, entityManagerFactory);
    }

}
