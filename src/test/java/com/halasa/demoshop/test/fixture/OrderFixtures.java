package com.halasa.demoshop.test.fixture;

import com.halasa.demoshop.service.domain.Customer;
import com.halasa.demoshop.service.domain.Order;
import com.halasa.demoshop.service.domain.OrderItem;
import org.apache.commons.lang3.RandomUtils;

import java.math.BigDecimal;
import java.util.List;

public class OrderFixtures {

    public static Order some(Customer customer, List<OrderItem> orderItems) {
        long random = RandomUtils.nextInt();
        return new Order(
                customer,
                BigDecimal.valueOf(random),
                BigDecimal.valueOf(random).multiply(new BigDecimal("1.2")),
                orderItems
        );
    }
}
