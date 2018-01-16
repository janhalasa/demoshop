package com.halasa.demoshop.service.repository.jpa;

import com.halasa.demoshop.service.domain.Order;
import com.halasa.demoshop.service.domain.OrderItem_;
import com.halasa.demoshop.service.domain.Order_;
import com.halasa.demoshop.service.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Arrays;

@Repository
public class OrderRepositoryJpa extends BasicReadOnlyRepositoryJpa<Order, Long> implements OrderRepository {

    @Autowired
    public OrderRepositoryJpa(EntityManager entityManager, EntityManagerFactory entityManagerFactory) {
        super(
                OrderRepositoryJpa.class,
                Order.class,
                Arrays.asList(
                        Order_.customer.getName(),
                        Order_.orderItems.getName(),
                        Order_.orderItems.getName() + "." + OrderItem_.product.getName()),
                entityManager,
                entityManagerFactory);
    }
}
