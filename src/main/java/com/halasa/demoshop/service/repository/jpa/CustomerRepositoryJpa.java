package com.halasa.demoshop.service.repository.jpa;

import com.halasa.demoshop.service.domain.Customer;
import com.halasa.demoshop.service.domain.Customer_;
import com.halasa.demoshop.service.domain.Order_;
import com.halasa.demoshop.service.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Arrays;
import java.util.Optional;

@Repository
public class CustomerRepositoryJpa extends BasicReadOnlyRepositoryJpa<Customer, Long> implements CustomerRepository {

    @Autowired
    public CustomerRepositoryJpa(EntityManager entityManager, EntityManagerFactory entityManagerFactory) {
        super(ProductRepositoryJpa.class, Customer.class, Arrays.asList(Customer_.picture.getName()), entityManager, entityManagerFactory);
    }

    @Override
    public Optional<Customer> getByEmail(String email) {
        return getWhere((criteriaBuilder, root) -> criteriaBuilder.equal(root.get(Customer_.email), email));
    }

    @Override
    public Optional<Customer> getByOrder(Long orderId) {
        return getWhere((criteriaBuilder, root) ->
            criteriaBuilder.equal(root.join(Customer_.orders).get(Order_.id), orderId));
    }
}
