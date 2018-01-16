package com.halasa.demoshop.service;

import com.halasa.demoshop.service.domain.Customer;
import com.halasa.demoshop.service.domain.Product;
import com.halasa.demoshop.service.repository.CustomerRepository;
import com.halasa.demoshop.service.repository.GenericWriteOnlyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PictureService pictureService;
    private final GenericWriteOnlyRepository genericWriteOnlyRepository;

    @Autowired
    public CustomerService(
            CustomerRepository customerRepository,
            PictureService pictureService,
            GenericWriteOnlyRepository genericWriteOnlyRepository) {
        this.customerRepository = customerRepository;
        this.pictureService = pictureService;
        this.genericWriteOnlyRepository = genericWriteOnlyRepository;
    }

    @Transactional
    public Customer save(Customer customer, Optional<String> pictureReferenceCode) {
        if (pictureReferenceCode.isPresent()) {
            customer.setPicture(this.pictureService.loadByReferenceCode(pictureReferenceCode.get()));
        }
        final Customer savedCustomer = this.genericWriteOnlyRepository.save(customer);
        return this.customerRepository.getByPk(savedCustomer.getId());
    }

    @Transactional
    public void remove(Long productId) {
        this.genericWriteOnlyRepository.removeByPk(Product.class, productId);
    }
}
