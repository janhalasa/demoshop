package com.halasa.demoshop.service.repository;

import com.halasa.demoshop.service.domain.Customer;

import java.util.Optional;

public interface CustomerRepository extends BasicReadOnlyRepository<Customer, Long> {

    Optional<Customer> getByEmail(String email);

    Optional<Customer> getByOrder(Long orderId);
}
