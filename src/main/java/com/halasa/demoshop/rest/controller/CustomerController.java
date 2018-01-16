package com.halasa.demoshop.rest.controller;

import com.halasa.demoshop.api.CustomerRestPaths;
import com.halasa.demoshop.api.dto.CustomerRestDto;
import com.halasa.demoshop.api.dto.PictureRestDto;
import com.halasa.demoshop.api.dto.response.ListResponse;
import com.halasa.demoshop.app.security.Roles;
import com.halasa.demoshop.rest.FetchListParser;
import com.halasa.demoshop.rest.OrderByParser;
import com.halasa.demoshop.rest.mapper.CustomerRestMapper;
import com.halasa.demoshop.rest.mapper.PictureRestMapper;
import com.halasa.demoshop.service.CustomerService;
import com.halasa.demoshop.service.domain.Customer;
import com.halasa.demoshop.service.domain.Picture;
import com.halasa.demoshop.service.repository.CustomerRepository;
import com.halasa.demoshop.service.repository.ListResult;
import com.halasa.demoshop.service.repository.PictureRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final PictureRepository pictureRepository;
    private final CustomerService customerService;
    private final CustomerRestMapper customerRestMapper;
    private final PictureRestMapper pictureRestMapper;
    private final OrderByParser orderByParser = new OrderByParser();
    private final FetchListParser fetchListParser = new FetchListParser();

    public CustomerController(
            CustomerRepository customerRepository,
            PictureRepository pictureRepository,
            CustomerService customerService,
            CustomerRestMapper customerRestMapper,
            PictureRestMapper pictureRestMapper) {
        this.customerRepository = customerRepository;
        this.pictureRepository = pictureRepository;
        this.customerService = customerService;
        this.customerRestMapper = customerRestMapper;
        this.pictureRestMapper = pictureRestMapper;
    }

    @GetMapping(value = CustomerRestPaths.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomerRestDto getCustomer(@PathVariable Long id, @RequestParam(required = false) String fetch) {
        final Customer customer = this.customerRepository.getByPk(id, this.fetchListParser.parse(fetch));
        return this.customerRestMapper.asCustomerRestDto(customer);
    }

    @GetMapping(value = CustomerRestPaths.GET_PICTURE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PictureRestDto getCustomerPicture(@PathVariable(name = "id") Long customerId) {
        Picture picture = this.pictureRepository.loadByCustomer(customerId);
        return this.pictureRestMapper.asPictureRestDto(picture);
    }

    @PostMapping(value = CustomerRestPaths.CREATE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerRestDto createCustomer(
            @RequestBody @Validated CustomerRestDto customerCreateRequest,
            @RequestParam(required = false) Optional<String> pictureReferenceCode) {
        final Customer customer = this.customerRestMapper.asCustomer(customerCreateRequest);
        customer.setId(null);
        return this.customerRestMapper.asCustomerRestDto(
                this.customerService.save(customer, pictureReferenceCode));
    }

    @PutMapping(value = CustomerRestPaths.UPDATE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomerRestDto updateCustomer(
            @PathVariable Long id,
            @RequestBody @Validated CustomerRestDto customerUpdateRequest,
            @RequestParam(required = false) Optional<String> pictureReferenceCode) {
        final Customer customerToUpdate = this.customerRepository.getByPk(id);
        final Customer updatedCustomer = this.customerRestMapper.asCustomer(customerUpdateRequest, customerToUpdate);
        return customerRestMapper.asCustomerRestDto(
                this.customerService.save(updatedCustomer, pictureReferenceCode));
    }

    @PreAuthorize(Roles.IS_ADMIN)
    @DeleteMapping(value = CustomerRestPaths.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable Long id) {
        this.customerService.remove(id);
    }

    @GetMapping(value = CustomerRestPaths.SEARCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ListResponse<CustomerRestDto> searchCustomers(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) String fetch,
            @RequestParam(required = false) String orderBy) {

        final ListResult<Customer> listResult = this.customerRepository.search(
                Optional.ofNullable(filter),
                Optional.ofNullable(limit),
                Optional.ofNullable(offset),
                this.fetchListParser.parse(fetch),
                this.orderByParser.parse(orderBy));

        return new ListResponse<CustomerRestDto>(
                listResult.getResults().stream()
                        .map(product -> this.customerRestMapper.asCustomerRestDto(product))
                        .collect(Collectors.toList()),
                listResult.getTotalCount(),
                listResult.getLimit(),
                listResult.getOffset()
        );
    }

}
