package com.halasa.demoshop.test.fixture;

import com.halasa.demoshop.service.domain.OrderItem;
import com.halasa.demoshop.service.domain.Product;
import org.apache.commons.lang3.RandomUtils;

public class OrderItemFixtures {
    
    public static OrderItem some(Product product) {
        return new OrderItem(
                null,
                product,
                RandomUtils.nextInt(1, 10)
        );
    }
}
