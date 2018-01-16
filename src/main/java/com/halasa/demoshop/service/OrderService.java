package com.halasa.demoshop.service;

import com.halasa.demoshop.service.OrderPriceCalculator.Price;
import com.halasa.demoshop.service.domain.Order;
import com.halasa.demoshop.service.domain.OrderItem;
import com.halasa.demoshop.service.repository.GenericWriteOnlyRepository;
import com.halasa.demoshop.service.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final GenericWriteOnlyRepository genericWriteOnlyRepository;
    private final OrderPriceCalculator orderPriceCalculator;

    @Autowired
    public OrderService(
            OrderRepository orderRepository,
            GenericWriteOnlyRepository genericWriteOnlyRepository,
            OrderPriceCalculator orderPriceCalculator) {
        this.orderRepository = orderRepository;
        this.genericWriteOnlyRepository = genericWriteOnlyRepository;
        this.orderPriceCalculator = orderPriceCalculator;
    }

    @Transactional
    public Order save(Order order) {
        if (order.getOrderItems() != null) {
            order.getOrderItems().forEach(orderItem -> {
                orderItem.setOrder(order);
                orderItem.setId(new OrderItem.OrderItemId(order.getId(), orderItem.getProduct().getId()));
            });
        }

        final Price price = this.orderPriceCalculator.calculate(order);
        order.setPriceWithoutVat(price.getWithoutVat());
        order.setPriceWithVat(price.getWithVat());

        final Order savedOrder = this.genericWriteOnlyRepository.save(order);
        return this.orderRepository.getByPk(savedOrder.getId());
    }

    @Transactional
    public void remove(Long orderId) {
        this.genericWriteOnlyRepository.removeByPk(Order.class, orderId);
    }
}
