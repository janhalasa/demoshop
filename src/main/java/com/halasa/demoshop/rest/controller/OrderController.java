package com.halasa.demoshop.rest.controller;

import com.halasa.demoshop.api.OrderRestPaths;
import com.halasa.demoshop.api.dto.OrderRestDto;
import com.halasa.demoshop.api.dto.response.ListResponse;
import com.halasa.demoshop.app.security.Roles;
import com.halasa.demoshop.app.security.SecurityPrincipal;
import com.halasa.demoshop.rest.FetchListParser;
import com.halasa.demoshop.rest.OrderByParser;
import com.halasa.demoshop.rest.mapper.OrderRestMapper;
import com.halasa.demoshop.service.OrderService;
import com.halasa.demoshop.service.domain.Customer;
import com.halasa.demoshop.service.domain.Order;
import com.halasa.demoshop.service.repository.CustomerRepository;
import com.halasa.demoshop.service.repository.ListResult;
import com.halasa.demoshop.service.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
public class OrderController {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderRestMapper orderRestMapper;
    private final OrderService orderService;
    private final OrderByParser orderByParser = new OrderByParser();
    private final FetchListParser fetchListParser = new FetchListParser();

    @Autowired
    public OrderController(
            OrderRepository orderRepository,
            CustomerRepository customerRepository,
            OrderService orderService,
            OrderRestMapper orderRestMapper) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.orderRestMapper = orderRestMapper;
        this.customerRepository = customerRepository;
    }

    @GetMapping(path = OrderRestPaths.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderRestDto getOrder(
            @PathVariable Long id,
            @RequestParam(required = false) String fetch,
            @AuthenticationPrincipal SecurityPrincipal user) {
        fetch = "orderItems";
        final Order order = this.orderRepository.getByPk(id, this.fetchListParser.parse(fetch));
        if (! user.getAuthorities().contains(Roles.ADMIN_GA) && ! order.getCustomer().getEmail().equals(user.getEmail())) {
            throw new AccessDeniedException("Customers may access only their own orders");
        }
        return this.orderRestMapper.asOrderRestDto(order);
    }

    @PostMapping(path = OrderRestPaths.CREATE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public OrderRestDto createOrder(
            @RequestBody @Validated OrderRestDto orderCreateRequest,
            @AuthenticationPrincipal SecurityPrincipal user) {
        final Order order = this.orderRestMapper.asOrder(orderCreateRequest);
        order.setId(null);
        order.setCustomer(this.resolveCustomer(order, user));
        return this.orderRestMapper.asOrderRestDto(
                this.orderService.save(order));
    }

    private Customer resolveCustomer(Order order, SecurityPrincipal user) {
        if (order.getCustomer() == null) {
            return this.customerRepository.getByEmail(user.getEmail()).get();
        }
        if (! user.getAuthorities().contains(Roles.ADMIN_GA)) {
            if (! user.getEmail().equals(order.getCustomer().getEmail())) {
                throw new AccessDeniedException("Customers may access/modify only their own orders");
            }
            return this.customerRepository.getByEmail(user.getEmail()).get();
        }
        return order.getCustomer();
    }

    @PutMapping(path = OrderRestPaths.UPDATE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderRestDto updateOrder(
            @PathVariable Long id,
            @RequestBody @Validated OrderRestDto orderUpdateRequest,
            @AuthenticationPrincipal SecurityPrincipal user) {
        final Order orderToUpdate = this.orderRepository.getByPk(id);
        orderToUpdate.setCustomer(this.resolveCustomer(orderToUpdate, user));
        final Order orderWithUpdatedValues = this.orderRestMapper.asOrder(orderUpdateRequest, orderToUpdate);
        final Order savedOrder = this.orderService.save(orderWithUpdatedValues);
        return orderRestMapper.asOrderRestDto(savedOrder);
    }

    @DeleteMapping(value = OrderRestPaths.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityPrincipal user) {
        if (! user.getAuthorities().contains(Roles.ADMIN_GA)) {
            final Optional<Customer> optionalCustomer = this.customerRepository.getByOrder(id);
            if (! optionalCustomer.isPresent()) {
                return;
            }
            if (! user.getEmail().equals(optionalCustomer.get().getEmail())) {
                throw new AccessDeniedException("Customers may access/modify only their own orders");
            }
        }
        this.orderService.remove(id);
    }

    @PreAuthorize(Roles.IS_ADMIN)
    @GetMapping(value = OrderRestPaths.SEARCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ListResponse<OrderRestDto> searchOrders(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) String fetch,
            @RequestParam(required = false) String orderBy) {

        final ListResult<Order> listResult = this.orderRepository.search(
                Optional.ofNullable(filter),
                Optional.ofNullable(limit),
                Optional.ofNullable(offset),
                this.fetchListParser.parse(fetch),
                this.orderByParser.parse(orderBy));

        return new ListResponse<OrderRestDto>(
                listResult.getResults().stream()
                        .map(product -> this.orderRestMapper.asOrderRestDto(product))
                        .collect(Collectors.toList()),
                listResult.getTotalCount(),
                listResult.getLimit(),
                listResult.getOffset()
        );
    }
}
